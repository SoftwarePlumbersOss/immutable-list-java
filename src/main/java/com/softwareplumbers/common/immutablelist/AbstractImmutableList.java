package com.softwareplumbers.common.immutablelist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/** Very simple immutable list class.
 * 
 * Intended for short lists of things that have a definite ordering.
 * 
 * @author SWPNET\jonessex
 * @param <T> Value type of list
 *
 */
public abstract class AbstractImmutableList<T extends Comparable<T>, V extends AbstractImmutableList<T,V>> implements Comparable<V>, Iterable<T> {
    
    public static final String DEFAULT_ESCAPE="\\";
            
    @FunctionalInterface
    public interface Transformer<T extends Comparable<T>, E extends Exception> {
        T transform(T part) throws E;
    }
	
	/** First part of list.
	 * 
	 */
	public final V parent;

	/** This part of the list.
	 *  
	 */
	public final T part;
	
	protected AbstractImmutableList(V parent, T part) {
		this.parent = parent;
		this.part = part;
	}

    public abstract V getEmpty();
	
	/** Add a new part to a list
	 * 
	 * @param part New part to add to a list
	 * @return A new list name (this list does not change)
	 */
	public abstract V add(T part);

	
    /** Generate a hash code for a list
     * 
     * @return hash code
     */
	@Override
	public int hashCode() {
        if (isEmpty()) return 77;
		return (parent.hashCode() * 17) ^ part.hashCode();
	}
 
    /** Compare this list with another.
     * 
     * If parent lists are equal, return the result of comparing parts. Otherwise
     * the result of comparing parents. An empty list is deemed equal to itself and less than
     * any other value.
     * 
     * @param other Other list to compare
     * @return -1 if this list less than other, 0 if equal, 1 if greater.
     */
	@Override
	public int compareTo(V other) {
        if (isEmpty() && other.isEmpty()) return 0;
        if (isEmpty()) return -1;
		if (other.isEmpty()) return 1;
		int parentComparison = parent.compareTo(other.parent);
		if (parentComparison != 0) return parentComparison;
		return part.compareTo(other.part);
	}
	
    /** Compare a list with another object
     * 
     * @param other Other list to compare
     * @return true of other is an ImmutableList which is equal according to the compareTo algorithm.
     */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		return other instanceof AbstractImmutableList ? 0 == compareTo((V)other) : false;
	}
	
	/** Apply accumulator function in depth-first order
	 * 
	 * @param <U> value type of accumulator
	 * @param applyTo Initial accumulator value
	 * @param accumulator Accumulation function
	 * @param whiletrue Stop and return what we have when false
	 * @return The result of applying the function to the accumulator value and each part.
	 */
	public <U> U apply(U applyTo, BiFunction<U,T,U> accumulator, Predicate<U> whiletrue) {
        if (isEmpty()) return applyTo;
		U result = parent.apply(applyTo, accumulator, whiletrue);
		if (whiletrue.test(result))
			return accumulator.apply(result, part);
		else
			return result;
	}
	
	/** Apply accumulator function in depth-first order
	 * 
	 * @param <U> value type of accumulator
	 * @param applyTo Initial accumulator value
	 * @param accumulator Accumulation function
	 * @return The result of applying the function to the accumulator value and each part.
	 */
	public <U> U apply(U applyTo, BiFunction<U,T,U> accumulator) {
		return apply(applyTo, accumulator, (t)->true);
	}
    
    /** Transform each element of a List
     * 
     * @param <E> Exception type thrown by transformer function
     * @param transformer function to transform each part of this list
     * @return a list with each element of this list transformed by the transformer 
     * @throws E Exception propagated from transformer
     */
    public <E extends Exception> V transform(Transformer<T,E> transformer) throws E {
        if (isEmpty()) return (V)this;
        return parent.transform(transformer).add(transformer.transform(part));        
    }
	
	/** Find if any part satisfies a predicate
	 * 
	 * @param predicate test to satisfy
	 * @return smallest index (from end) of part matching predicate
	 */
	public int indexFromEnd(Predicate<T> predicate) {
        if (isEmpty()) return -1;
		if (predicate.test(part)) return 0;
		int result = parent.indexFromEnd(predicate);
		return (result < 0) ? result : 1 + result;
	}
	
	/** Find if any part satisfies a predicate
	 * 
	 * @param predicate test to satisfy
	 * @return smallest index (from start) of part matching predicate
	 */
	public int indexOf(Predicate<T> predicate) {
		return reverse().indexFromEnd(predicate);
	}
	
	/** Match this list against another using a predicate
	 * 
	 * @param list Name to match
	 * @param matcher Predicate to determine whether parts match
	 * @param match_all if true, all parts in given name must match all parts in this name
	 * @return true if matcher is satisfied for each corresponding part of this and the given name
	 */
	public boolean matches(AbstractImmutableList<T,?> list, BiPredicate<T,T> matcher, boolean match_all) {
		if (list.isEmpty()) return this.isEmpty();
        if (this.isEmpty()) return !match_all;
		return matcher.test(part, list.part) && parent.matches(list.parent, matcher, match_all);
		
	}

	/** Apply accumulator function in reverse order
	 * 
	 * @param <U> value type of accumulator
	 * @param applyTo Initial accumulator value
	 * @param accumulator Accumulation function
	 * @param whiletrue Termination function - stop applying when false
	 * @return The result of applying the function to the accumulator value and each part.
	 */
	public <U> U applyReverse(U applyTo, BiFunction<U,T,U> accumulator, BiPredicate<U,T> whiletrue) {
        if (isEmpty()) return applyTo;
		if (whiletrue.test(applyTo, part)) 
			return parent.applyReverse(accumulator.apply(applyTo, part), accumulator, whiletrue);
		else
			return applyTo;
	}
	
	/** Apply accumulator function in reverse order
	 * 
	 * @param <U> value type of accumulator
	 * @param applyTo Initial accumulator value
	 * @param accumulator Accumulation function
	 * @return The result of applying the function to the accumulator value and each part.
	 */
	public <U> U applyReverse(U applyTo, BiFunction<U,T,U> accumulator) {
		return applyReverse(applyTo, accumulator, (x,y)->true);
	}
    
    private static String escape(final String toEscape, final String separator, final String escape) {
        return toEscape.replace(escape,escape+escape).replace(separator, escape + separator);
    }
	
	/** Join elements of the list with the given separator.
     * 
     * if the given separator exists with any part of the name, it will be escaped
     * by doubling the separator character.
	 * 
     * @param converter function to convert T to a string.
	 * @param separator string to place between elements of path
     * @param escape string to prefix separator with if found in the parts of this name
	 * @return concatenate elements of path with separator between them.
	 */
	public String join(Function<T,String> converter, final String separator, final String escape) {
        if (isEmpty()) return "";
		final BiFunction<String,T,String> joiner = (left,right)-> {
            return left.isEmpty()
                ? escape(converter.apply(right), separator, escape)
                : left + separator + escape(converter.apply(right), separator, escape);
        };
		return apply("", joiner);
	}
    
    /** Join elements of the list with the given separator.
     * 
     * if the given separator exists with any part of the name, it will be escaped
     * with DEFAULT_ESCAPE.
	 * 
     * @param converter function to convert T to a string.
	 * @param separator string to place between elements of path
	 * @return concatenate elements of path with separator between them.
	 */
    public String join(Function<T,String> converter, final String separator) {
        return join(converter, separator, DEFAULT_ESCAPE);
    }
	
	/** Add several elements in order.
	 * 
	 * Equivalent to list.add(part[0]).add(part[1])... etc
	 * 
	 * @param parts to add
	 * @return new list including additional parts
	 */
	public V addAll(T... parts) {
		return addAll(Arrays.asList(parts));
	}
	
	/** Add several elements in order 
	 * 
	 * Equivalent to list.add(part.get(0)).add(part.get(1))... etc
	 * 
	 * @param parts to add
	 * @return new list including additional parts
	 */
	public V addAll(Iterable<T> parts) {
		V result = (V)this;
		for (T p : parts) result = result.add(p);
		return result;
	}
	
    private enum ParseState { BEGIN, SEPARATOR_AT_START, JOIN, NEXT }
    
    private static final String unescape(String escaped, String escape) {
        String regexEscape = escape.replace("\\", "\\\\");
        return escaped
            .replaceAll("(?<!"+ regexEscape +")" + regexEscape, "")
            .replaceAll(regexEscape + regexEscape, escape);
    }
    
	/** Add several elements as parsed from a string.
	 * 
     * @param converter function to convert strings to elements
	 * @param toParse string to parse
	 * @param separator separator to break up name parts
     * @param escape escape character, which is used as prefix for separator
	 * @return list with the leftmost element of string as root
	 */
	public V addParsed(Function<String,T> converter, String toParse, String separator, String escape) {
        String regexEscape = escape.replace("\\", "\\\\");
        String[] elements = toParse.split("(?<!"+ regexEscape +")" + separator);
        V result = (V)this;
        for (String element : elements) {
            if (!element.isEmpty()) result = result.add(converter.apply(unescape(element, escape)));
        }
        return result;
	}
    
    /** Add several elements as parsed from a string
     * 
     * Equivalent to addParsed(converter, toParse, separate, DEFAULT_ESCAPE)
     * 
     * @param converter function to convert strings to elements
     * @param toParse string to parse
     * @param separator separator used to identify elements within the string
     * @return The list parsed from then given string
     */
    public V addParsed(Function<String,T> converter, String toParse, String separator) {
        return addParsed(converter, toParse, separator, DEFAULT_ESCAPE);
    }
	
	/** Default string representation
	 * 
	 * Equivalent to join(Object::toString, ".")
	 * 
	 * @return join(".")
	 */
    @Override
	public String toString() {
		return join(Object::toString, ".");
	}
	
	/** Reverse the order of the elements
	 * 
	 * @return A list with parts in reverse order to this one
	 */
	public V reverse() {
		return applyReverse(getEmpty(), (a,e)->a.add(e));
	}
	
	/** Check to see if a list ends with a given list
	 * 
	 * @param list name to check
	 * @return true if the last elements of this list match the given list
	 */
	public boolean endsWith(AbstractImmutableList<T,?> list) {
		return list.matches(this, (a,b) -> a.equals(b), false);
	}
	
	/** Check to see if a list starts with a given list
	 * 
	 * @param list list to check
	 * @return true if the first elements of this list match the given name
	 */
	public boolean startsWith(AbstractImmutableList<T,?> list) {
		return reverse().endsWith(list.reverse());
	}
	
	/** Return elements in a list up to the one matching the predicate 
	 * 
	 * @param matching test to match
	 * @return A list including elements up to the matching part
	 */
	public V upTo(Predicate<T> matching) {
		return apply(getEmpty(), (result,elem)->result.add(elem), (result)->result.isEmpty() || !matching.test(result.part)); 
	}
	
	/** Return elements in a list up to the given index, counting from start
     * 
     * @param index index of first dropped part
     * @return the leftmost parts of the list, up to index
     */
	public V left(int index) {	
		return leftFromEnd(size()-index);
	}

	/** Return elements in a list from the given index, counting from start
     * 
     * @param index index of first retained part
     * @return the rightmost parts of the list, starting from index
     */
	public V rightFromStart(int index) {
		return right(size()-index);
	}	

	/** Return elements in a list from the last one matching the predicate 
	 * 
	 * @param matching Predicate to match an element in the name
	 * @return A list including elements from the last one matching the predicate
	 */
	public V fromEnd(Predicate<T> matching) {
		return applyReverse(getEmpty(), (result,elem)->result.add(elem), (result,elem) -> !matching.test(elem)).reverse();
	}
	
    /** Return the n rightmost elements of the list.
     * 
     * @param n count of elements retained
     * @return the n rightmost elements of the list.
     */
	public V right(int n) {
        if (isEmpty()) return (V)this;
		if (n <= 0) return getEmpty();
		return parent.right(n-1).add(part);
	}
	
    /** Return what is left of the list after the rightmost n elements have been removed.
     * 
     * @param n count of elements removed
     * @return the list with the n rightmost elements removed.
     */
	public V leftFromEnd(int n) {
		if (isEmpty() || n <= 0) return (V)this;
		return parent.leftFromEnd(n-1);
	}

	

	
	/** Get the part that is nth from then end
	 * 
	 * @param index index of part to fetch
	 * @return A part
	 */
	public T getFromEnd(int index) {
		return (index == 0) ? part : parent.getFromEnd(index-1);
	}
	
	/** Get the part that is nth from the start
	 * 
	 * @param index index of part to fetch
	 * @return A part
	 */
	public T get(int index) {
		return reverse().getFromEnd(index);
	}
	
	/** Get number of parts in list
	 * 
	 * @return number of parts in this list
	 */
	public int size() {
        if (isEmpty()) return 0;
		return parent.size()+1;
	}
	
	/** Check if list is empty
	 * 
	 * @return true if list has no parts
	 */
	public boolean isEmpty() {
		return false;
	}

	/** Iterator over parts
	 * 
	 * @author SWPNET\jonessex
	 *
	 */
	private static class MyIterator<T extends Comparable<T>> implements Iterator<T> {
		
		AbstractImmutableList<T,?> current;
		
		public MyIterator(AbstractImmutableList<T,?> current) { this.current = current; }

		@Override
		public boolean hasNext() {
			return !current.isEmpty();
		}

		@Override
		public T next() {
			T next = current.part;
			current = current.parent;
			return next;
		}
	}
	
	/** Iterate over parts from first to last
	 * 
	 */
	@Override
	public Iterator<T> iterator() {
		return reverse().reverseIterator();
	}
	
	/** Iterate over parts from last to first
     * @return an iterator over parts of this list 
	 */
	public Iterator<T> reverseIterator() {
		return new MyIterator<>(this);
	}
}