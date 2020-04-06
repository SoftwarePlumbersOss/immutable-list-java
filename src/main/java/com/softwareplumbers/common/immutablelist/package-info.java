/** Useful variants of an Immutable list.
 * 
 * The base immutable list is a singly-linked list which is immutable. New
 * elements are added by creating a new list which includes the old. This
 * has advantages in some cases - we can return an ImmutableList without
 * worrying whether the caller will modify it or not.
 * 
 */
package com.softwareplumbers.common.immutablelist;
