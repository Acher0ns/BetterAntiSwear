/*     */ package net.amoebaman.util;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import org.apache.commons.lang.Validate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArrayWrapper<E>
/*     */ {
/*     */   private E[] _array;
/*     */   
/*     */   public ArrayWrapper(Object... elements) {
/*  28 */     setArray((E[])elements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E[] getArray() {
/*  39 */     return this._array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArray(Object[] array) {
/*  48 */     Validate.notNull(array, "The array must not be null.");
/*  49 */     this._array = (E[])array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/*  60 */     if (!(other instanceof ArrayWrapper)) {
/*  61 */       return false;
/*     */     }
/*  63 */     return Arrays.equals((Object[])this._array, (Object[])((ArrayWrapper)other)._array);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  74 */     return Arrays.hashCode((Object[])this._array);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T[] toArray(Iterable<? extends T> list, Class<T> c) {
/*  87 */     int size = -1;
/*  88 */     if (list instanceof Collection) {
/*     */       
/*  90 */       Collection coll = (Collection)list;
/*  91 */       size = coll.size();
/*     */     } 
/*     */ 
/*     */     
/*  95 */     if (size < 0) {
/*  96 */       size = 0;
/*     */       
/*  98 */       for (T element : list) {
/*  99 */         size++;
/*     */       }
/*     */     } 
/*     */     
/* 103 */     Object[] result = (Object[])Array.newInstance(c, size);
/* 104 */     int i = 0;
/* 105 */     for (T element : list) {
/* 106 */       result[i++] = element;
/*     */     }
/* 108 */     return (T[])result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\net\amoebama\\util\ArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */