// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.xdebugger.frame;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;

/**
 * Provides implementation of 'Mark Object' feature. <p>
 *
 * If debugger values have unique ids just return these ids from {@link #getMarker(XValue)} method.
 * Alternatively implement {@link #markValue(XValue)} to store a value in some registry and implement {@link #unmarkValue(XValue, Object)}
 * to remote it from the registry. In such a case the {@link #getMarker(XValue)} method can return {@code null} if the {@code value} isn't marked.
 */
public abstract class XValueMarkerProvider<V extends XValue, M> {
  private final Class<V> myValueClass;

  protected XValueMarkerProvider(Class<V> valueClass) {
    myValueClass = valueClass;
  }

  /**
   * @return {@code true} if 'Mark Object' action should be enabled for {@code value}
   */
  public abstract boolean canMark(@NotNull V value);

  /**
   * This method is used to determine whether the {@code value} was marked or not. The returned object is compared using {@link Object#equals(Object)}
   * method with markers returned by {@link #markValue(XValue)} methods. <p>
   * This method may return {@code null} if the {@code value} wasn't marked by {@link #markValue(XValue)} method.
   * @return a marker for {@code value}
   */
  public abstract M getMarker(@NotNull V value);

  /**
   * This method is called when 'Mark Object' action is invoked. Return an unique marker for {@code value} and store it in some registry
   * if necessary.
   * @return a marker for {@code value}
   */
  public @NotNull M markValue(@NotNull V value) {
    return getMarker(value);
  }

  /**
   * Async version of the {@link #markValue(XValue)} method
   * @return a promise with a marker for {@code value}
   */
  public @NotNull Promise<M> markValueAsync(@NotNull V value) {
    return Promises.resolvedPromise(markValue(value));
  }

  /**
   * This method is called when 'Unmark Object' action is invoked.
   */
  public void unmarkValue(@NotNull V value, @NotNull M marker) {
  }

  /**
   * Async version of the {@link #unmarkValue(XValue, Object)} method
   */
  public Promise<Object> unmarkValueAsync(@NotNull V value, @NotNull M marker) {
    unmarkValue(value, marker);
    return Promises.resolvedPromise();
  }

  public final Class<V> getValueClass() {
    return myValueClass;
  }
}
