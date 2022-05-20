package net.reikeb.maxilib;

public record Couple<U, V>(U part1, V part2) {

    /**
     * Compares this Couple to the specified object. The result is {@code true} if and only if the argument is not {@code null} and is a {@code Couple} object that represents the same Couple as this object.
     *
     * @param anObject The object to compare this {@code Couple} against
     * @return {@code true} if the given object represents a {@code Couple} equivalent to this Couple, {@code false} otherwise
     */
    public boolean equalsTo(Object anObject) {
        if (this == anObject) {
            return true;
        }
        return anObject instanceof Couple<?, ?> couple && couple.part1.equals(this.part1) && couple.part2.equals(this.part2);
    }
}
