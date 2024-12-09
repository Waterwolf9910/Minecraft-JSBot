module global {

    export interface JSBlockPos {
        readonly x: number;
        readonly y: number;
        readonly z: number;
        toString(): string
    }

    export interface JSItemStack {
        readonly id: string;
        readonly name: string;
        readonly count: number;
        readonly max_count: number;
        readonly durability: boolean;
        readonly is_food: boolean;
        readonly is_enchantable: boolean;
        readonly recipe_remainder: JSItemStack;
    }

    type Direction = "north" | "south" | "east" | "west" | "up" | "down";

    type BlockBreakState = {
        success: boolean;
        id: String,
        durability: int
        pos: JSBlockPos
    }

    type BlockPlaceState = {
        success: boolean;
        count_remaining: number
        pos: JSBlockPos
    }

    export interface JSApi {
        /**
         * Get current block position
         */
        getBlockPos(): JSBlockPos;
        /**
         * Get current face direction
         */
        getFacing(): Direction;
        /**
         * Moves the bot to the dir
         * @param dir the direction to move to
         * @returns true if move was successful
         */
        move(dir: Direction): boolean;
        /**
         * Faces the bot in another direction
         * @param dir the direction to face in
         * @returns the result of the break attempt
         */
        face(dir: Direction): void;
        /**
         * Sets the output signle the block will replace
         * @returns the result of the place attempt
         */
        setRedstoneOutput(dir: Direction, value: number): void;
        /**
         * Breaks the block where the bot is looking
         */
        breakBlock(): BlockBreakState
        /**
         * Places the block where the bot is looking
         */
        placeBlock(slot: number): BlockPlaceState
    }
}