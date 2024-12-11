package com.waterwolfies.js_bot.js;

import com.waterwolfies.js_bot.JSBot;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.io.IOAccess;

import net.fabricmc.loader.api.FabricLoader;

public class JSHandler {
    
    private static String base_script;
    private Thread js_thread;
    private String script;
    private String name;
    private Path scripts_path;
    private ForwardStream out = new ForwardStream();
    private ForwardStream err = new ForwardStream(System.err);
    private Context context;
    private Value bindings;
    private Runnable onComplete = () -> {};

    public JSHandler(String script) throws NoSuchMethodException {
        this(script, false);
    }
    public JSHandler(String script, boolean start) throws NoSuchMethodException {
        this(script, getGlobalCWD(), start);
    }

    private static Path getGlobalCWD() {
        if (JSBot.global_scripts == null) {
            return FabricLoader.getInstance().getGameDir().toAbsolutePath().normalize();
        }
        return JSBot.global_scripts;
    }

    public JSHandler(String script, Path scripts_path, boolean start) throws NoSuchMethodException {
        // java.nio.file.FileSystem fs;
        // try {
        //     fs = FileSystems.newFileSystem(cwd);
        // } catch (IOException e) {
        //     fs = FileSystems.getDefault();
        // }
        this.script = script;
        this.scripts_path = scripts_path;
        this.context = Context.newBuilder("js", "dap")
            // TODO: Limit Priviledges in JS when api is done
            .allowExperimentalOptions(true)
            .allowHostAccess(HostAccess.newBuilder(HostAccess.SCOPED)
                .allowAccessAnnotatedBy(HostAccess.Implementable.class)
                .allowArrayAccess(true)
                .allowBigIntegerNumberAccess(true)
                .allowIterableAccess(true)
                .allowMapAccess(true)
                .methodScoping(true)
                .allowAccess(Object.class.getMethod("toString"))
                .allowAccess(Object.class.getMethod("getClass"))
                .allowAccess(Object.class.getMethod("hashCode"))
                .allowAccess(Object.class.getMethod("equals", Object.class))
                .allowAccess(JSApi.class.getMethod("getBlockPos"))
                .allowAccess(JSApi.class.getMethod("getFacing"))
                .allowAccess(JSApi.class.getMethod("move", String.class))
                .allowAccess(JSApi.class.getMethod("face", String.class))
                .allowAccess(JSApi.class.getMethod("setRedstoneOutput", String.class, int.class))
                .allowAccess(JSApi.class.getMethod("breakBlock"))
                .allowAccess(JSApi.class.getMethod("placeBlock", int.class))
                .allowAccess(JSApi.class.getMethod("getInventory"))
                .allowAccess(JSApi.JSBlockPos.class.getMethod("toString"))
                .allowAccess(JSApi.JSItemStack.class.getMethod("toString"))
                .allowAccess(JSApi.BlockPlaceState.class.getMethod("toString"))
                .allowAccess(JSApi.BlockBreakState.class.getMethod("toString"))
                .allowAccess(Throwable.class.getMethod("printStackTrace"))
                // .allowAccess(Object.class.getMethod("clone"))
                // .allowPublicAccess(true)
                // .allowMutableTargetMappings(
                //     MutableTargetMapping.HASH_TO_JAVA_MAP, 
                //     MutableTargetMapping.ARRAY_TO_JAVA_LIST
                // )
                .allowIteratorAccess(true)
                .build()
            )
            .out(out)
            .err(err)
            .allowIO(IOAccess
                .newBuilder()
                .fileSystem(new FS())
                .build())
                .allowValueSharing(true)
            // Non Constrained
            .allowNativeAccess(true)
            .allowHostClassLoading(true)
            // .option("js.stack-trace-limit", "50")
            .allowHostClassLookup((d) -> true)
            .logHandler(new LogHandler())
            .option("js.v8-compat", "true")
            .option("js.ecmascript-version", "2024")
            // .option("engine.CompilationFailureAction", "Print") // Does not work
            .option("js.unhandled-rejections", "warn")
            .option("js.bigint", "true")
            .option("js.commonjs-require", "true")
            .option("js.commonjs-require-cwd", scripts_path.toString())
            .option("js.global-property", "true")
            .option("js.import-attributes", "true")
            .option("js.json-modules", "true")
            .option("js.graal-builtin", "false")
            .option("js.java-package-globals", "false")
            .option("js.polyglot-builtin", "false")
            .option("js.esm-eval-returns-exports", "true")
            .option("js.async-iterator-helpers", "true")
            .option("js.esm-eval-returns-exports", "true")
            .option("js.error-cause", "true")
            .option("js.esm-bare-specifier-relative-lookup", "true")
            .option("js.top-level-await", "true")
            // .option("engine.Mode", "throughput")
            /// Use below when debuging base scripts and apis
            // .option("inspect", "localhost:4711")
            // .option("inspect.WaitAttached", "false")
            // .option("inspect.Suspend", "false")
            .build();
        this.bindings = this.context.getBindings("js");
        if (start) {
            run();
        }
        
    }

    // private static java.nio.file.FileSystem createSystemAndCopy(Path cwd) {
    //     java.nio.file.FileSystem fs = FileSystems.
    // }

    public JSHandler(Path script_file) throws NoSuchMethodException {
        this(script_file, false);
    }
    
    public JSHandler(Path script_file, boolean start) throws NoSuchMethodException {
        this(script_file, getCWDFromFile(script_file), start);
    }

    public JSHandler setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    private static Path getCWDFromFile(Path file) {
        Path path = file.resolve("..").normalize();
        if (!path.startsWith(JSBot.global_scripts.toString())) {
            return JSBot.global_scripts;
        }
        return path;

    }

    public JSHandler(Path script_file, Path scripts_path, boolean start) throws NoSuchMethodException {
        this(readFile(script_file), scripts_path, start);
        this.name = script_file.toFile().getName();
    }

    public JSHandler addObject(String name, Object value) {
        // this.bindings.put(name, value);
        this.bindings.putMember(name, value);
        return this;
    }

    public JSHandler addObjects(Map<String, ? extends Object> objects) {
        // this.bindings.putAll(objects);
        for (var obj : objects.entrySet()) {
            this.bindings.putMember(obj.getKey(), obj.getValue());
        }
        return this;
    }

    public JSHandler removeObject(String key) {
        // this.bindings.remove(key);
        this.bindings.removeMember(key);
        return this;
    }

    public JSHandler setOutput(OutputStream out) {
        this.out.setOut(out);
        return this;
    }

    public JSHandler setError(OutputStream out) {
        this.err.setOut(out);
        return this;
    }

    public boolean running() {
        return js_thread != null && js_thread.isAlive();
    }

    // TODO: Add instance based globals (Prob in Helper.java)
    public void run() {
        if (this.running()) {
            return;
        }
        this.js_thread = new Thread(() -> {
            runAllGlobals();
            try {
                // JSBot.LOGGER.info(base_script.replace("\\${script}", this.script));
                // engine.eval(script);
                // this.context.eval(Source.newBuilder("js", JSBot.global_script.resolve("hello.js").toFile()).build());
                this.context.eval(Source.newBuilder("js", base_script.replace("\\\\${script}", this.script), name).build());
                this.onComplete.run();
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(this.out));
            }
        });
        js_thread.setDaemon(true);
        js_thread.start();
    }

    private void runAllGlobals() {
        List<File> files = new ArrayList<>(Arrays.asList(this.scripts_path.toFile().listFiles((file, filename) -> filename.endsWith(".js"))));
        files.addAll(Arrays.asList(JSBot.instance_scripts.toFile().listFiles((file, filename) -> filename.endsWith(".js"))));
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                Value ret = this.context.eval(Source.newBuilder("js", file).build());
                if (ret.isException()) {
                    ret.throwException().printStackTrace(new PrintStream(this.err));
                }
            } catch (IOException e) {
                JSBot.LOGGER.warn("Error in global script {}", file.getName());
            }
        }
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    static {
        try {
            base_script = Files.readString(Path.of(JSHandler.class.getResource("/base.js").toURI()));
        } catch (Exception e) {
        }
    }

    private class ForwardStream extends OutputStream {
        
        private OutputStream out;

        public ForwardStream() {
            this(System.out);
        }

        public ForwardStream(OutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }
        
        public void setOut(OutputStream out) {
            this.out = out;
        }
    }

    private class LogHandler extends Handler {

        @Override
        public void close() throws SecurityException {}

        @Override
        public void flush() {
            try {
                JSHandler.this.out.flush();
                JSHandler.this.err.flush();
            } catch (IOException e) {}
        }

        @Override
        public void publish(LogRecord record) {
            ForwardStream stream = JSHandler.this.out;
            if (record.getLevel().intValue() > Level.INFO.intValue()) {
                stream = JSHandler.this.err;
            }
            try {
                stream.write(("[" + record.getLevel().getName() + "] " + record.getMessage()).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (record.getThrown() != null) {
                record.getThrown().printStackTrace(new PrintStream(stream));
            }
        }

    }

    private class FS implements FileSystem {

        private final FileSystem fs = FileSystem.newDefaultFileSystem();

        @Override
        public Path parsePath(URI uri) {
            return this.fs.parsePath(uri);
        }

        @Override
        public Path parsePath(String path) {
            return this.fs.parsePath(path);
        }

        @Override
        public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
            Path root_path = JSHandler.this.scripts_path;
            Path check_path = path;
            if (Files.isSymbolicLink(root_path)) {
                root_path = Files.readSymbolicLink(root_path);
            }
            if (Files.isSymbolicLink(check_path)) {
                check_path = Files.readSymbolicLink(check_path);
            }
            if (!check_path.toAbsolutePath().normalize().startsWith(root_path)) {
                throw new IOException("Attempting to escape");
            }
            this.fs.checkAccess(check_path, modes, linkOptions);
        }

        @Override
        public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
            this.fs.createDirectory(dir, attrs);
        }

        @Override
        public void delete(Path path) throws IOException {
            this.fs.delete(path);
        }

        @Override
        public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
            return this.fs.newByteChannel(path, options, attrs);
        }

        @Override
        public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
            return this.fs.newDirectoryStream(dir, filter);
        }

        @Override
        public Path toAbsolutePath(Path path) {
            return this.fs.toAbsolutePath(path);
        }

        @Override
        public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
            if (linkOptions != null) {
                return this.fs.toRealPath(path);
            }
            return this.fs.toRealPath(path, linkOptions);
        }

        @Override
        public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
            return this.fs.readAttributes(path, attributes, options);
        }
    }

}
