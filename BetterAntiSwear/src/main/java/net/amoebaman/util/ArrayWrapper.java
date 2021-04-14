package net.amoebaman.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang.Validate;

public final class ArrayWrapper<E> {
    private E[] _array;
    
    public ArrayWrapper(Object... elements) {
        setArray((E[])elements);
    }

    public E[] getArray() {
        return this._array;
    }

    public void setArray(Object[] array) {
        Validate.notNull(array, "The array must not be null.");
        this._array = (E[])array;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ArrayWrapper)) {
            return false;
        }
        return Arrays.equals((Object[])this._array, (Object[])((ArrayWrapper)other)._array);
    }

    public int hashCode() {
        return Arrays.hashCode((Object[])this._array);
    }

    public static <T> T[] toArray(Iterable<? extends T> list, Class<T> c) {
        int size = -1;
        if (list instanceof Collection) {
            
            Collection coll = (Collection)list;
            size = coll.size();
        } 

        
        if (size < 0) {
            size = 0;
            
            for (T element : list) {
                size++;
            }
        } 
        
        Object[] result = (Object[])Array.newInstance(c, size);
        int i = 0;
        for (T element : list) {
            result[i++] = element;
        }
        return (T[])result;
    }
}
