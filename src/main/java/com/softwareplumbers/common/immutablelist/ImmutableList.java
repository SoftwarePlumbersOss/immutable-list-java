/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwareplumbers.common.immutablelist;
import java.util.function.Function;

/** Concrete ImmutableList subclass.
 *
 * @author Jonathan Essex
 */
public class ImmutableList<T extends Comparable<T>> extends AbstractImmutableList<T, ImmutableList<T>> {
    
    private ImmutableList(ImmutableList<T> parent, T part) { super (parent, part); }
    
    private static final ImmutableList ROOT = new ImmutableList(null,null) {
        @Override
        public boolean isEmpty() { return true; }
    };
    
    public static <T extends Comparable<T>> ImmutableList<T> empty() {
        return ROOT;
    }
	
	/** Preferred way to construct a new list 
	 * 
	 * Equivalent to emtpy().add(part)
	 * 
     * @param <T> Value type of list
	 * @param part base of new list
	 * @return a new list
	 */
	public static <T extends Comparable<T>> ImmutableList<T> of(T part) {
		return ((ImmutableList<T>)empty()).add(part);
	}
		
	/** Preferred way to construct a new list 
	 * 
	 * Equivalent to empty().add(parts)
	 * 
     * @param <T> Value type of list
	 * @param parts base of new list
	 * @return a new list
	 */
	public static <T extends Comparable<T>> ImmutableList<T> of(T... parts) {
		return ((ImmutableList<T>)empty()).addAll(parts);
	}
	
    /** Parse a string into an ImmutableList using the given separator.
     * 
     * @param <T> Value type of list
     * @param list String to parse
     * @param separator Separator string 
     * @param elemParser function for parsing String to T
     * @return A list consisting of elements of the given string, split by the given separator
     */
	public static <T extends Comparable<T>> ImmutableList<T> parse(Function<String,T> elemParser, String list, String separator) {
		return ((ImmutableList<T>)empty()).addParsed(elemParser, list, separator);
	}    

    @Override
    public ImmutableList<T> getEmpty() {
        return empty();
    }

    @Override
    public ImmutableList<T> add(T part) {
        return new ImmutableList<>(this, part);
    }
}
