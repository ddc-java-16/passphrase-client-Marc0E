package edu.cnm.deepdive.passphrase.model;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single passphrase, consisting of a name, a persistence key, and a list of words.
 */
public final class Passphrase {

  private static final String MUTATION_REQUIRED_MESSAGE = "After word list is created, length can "
      + "only be set via append(String), insert(String), insert(int, String), remove(int), "
      + "remove(String), clear(), or truncate(int).";
  private static final String NULL_LIST_ERROR_FORMAT = "Unable to access element at position %d in passphrase of length 0.";
  private static final String TO_STRING_FORMAT = "%1$s[key=%2$s, name=%3$s, words=%4$s, length=%5$d]";

  @Expose(deserialize = true, serialize = false)
  @SerializedName(value = "id", alternate = {"key"})
  private final String key = null;
  @Expose(deserialize = true, serialize = false)
  private final Date created = null;
  @Expose(deserialize = true, serialize = false)
  private final Date modified = null;
  @Expose
  private String name;
  @Expose
  private List<String> words;
  @Expose(deserialize = false, serialize = true)
  private int length;

  /**
   * Appends specified {@code word} to the end of the list of words in this passphrase. Since this
   * method mutates the list of words in the passphrase, a list will be created first (if it doesn't
   * already exist).
   *
   * @param word {@link String} to append to passphrase.
   * @return {@code true} if {@code word} was successfully added, {@code false} otherwise.
   */
  public boolean append(String word) {
    ensureWordsExists();
    return words.add(word);
  }

  /**
   * Inserts specified {@code word} at the start of this passphrase. Since this method mutates the
   * list of words in the passphrase, a list will be created first (if it doesn't already exist).
   *
   * @param word {@link String} to insert in passphrase.
   * @return {@code true} if {@code word} was successfully inserted, {@code false} otherwise.
   */
  public boolean insert(String word) {

    return insert(0, word);
  }

  /**
   * Inserts specified {@code word} at index {@code position} in this passphrase.
   *
   * @param position index at which {@code word} is inserted in passphrase.
   * @param word     {@link String} to insert in passphrase.
   * @return {@code true} if {@code word} was successfully inserted, {@code false} otherwise.
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than passphrase
   *                                   length.
   */
  public boolean insert(int position, String word) throws IndexOutOfBoundsException {
    ensureWordsExists();
    words.add(position, word);
    return true;
  }

  /**
   * Removes word at specified {@code position} from this passphrase.
   *
   * @param position index from which to remove word from passphrase.
   * @return {@link String} word removed.
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *                                   passphrase length.
   */
  public String remove(int position) throws IndexOutOfBoundsException {
    ensureWordsExists();
    return words.remove(position);
  }

  /**
   * Removes the first occurrence (if any) of specified {@code word} from this passphrase.
   *
   * @param word {@link String} to be removed from passphrase.
   * @return {@code true} if {@code word} was found and removed, {@code false} otherwise.
   */
  public boolean remove(String word) {
    ensureWordsExists();
    return words.remove(word);
  }

  /**
   * Removes all words from this passphrase.
   */
  public void clear() {
    ensureWordsExists();
    words.clear();
  }

  /**
   * Truncates list to specified {@code length}.
   *
   * @param length Size of truncated list.
   * @return {@code true} if list was successfully truncated, {@code false} otherwise.
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *                                   passphrase length.
   */
  public boolean truncate(int length) throws IndexOutOfBoundsException {
    ensureWordsExists();
    words.subList(length, words.size()).clear();
    return true;
  }

  /**
   * Gets (retrieves) the word at the specified position in this passphrase.
   * @param position {@code int} index of word to retrieve.
   * @return {@link String} retrieved word.
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *                                   passphrase length.
   */
  public String get(int position) throws IndexOutOfBoundsException {
    if (words == null) {
      throw new IndexOutOfBoundsException(String.format(NULL_LIST_ERROR_FORMAT, position));
    }
    return words.get(position);
  }

  /**
   * Sets (replaces) the word at the specified position in this passphrase.
   *
   * @param position {@code int} index of word to replace.
   * @param word {@link String} new word to put at specified {@code position}.
   * @return {@link String} previous word at word position.
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *                                   passphrase length.
   */
  public String set(int position, String word) throws IndexOutOfBoundsException {
    ensureWordsExists();
    return words.set(position, word);

  }

  @Override
  public int hashCode() {
    return (words == null) ? Objects.hash(key, name, length) : Objects.hash(key, name, words);
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    boolean equivalent;
    if (obj == this) {
      equivalent = true;
    } else if (obj instanceof Passphrase other) {
      //noinspection ConstantValue
      equivalent = Objects.equals(key, other.key) && Objects.equals(name, other.name)
          && (words != null && words.equals(other.words)
          || words == null && other.words == null && length == other.length);
    } else {
      equivalent = false;
    }
    return equivalent;
  }

  @SuppressLint("DefaultLocale")
  @NonNull
  @Override
  public String toString() {
    return String.format(
        TO_STRING_FORMAT, getClass().getSimpleName(), key, name, words, getLength());
  }

  public String getKey() {
    return key;
  }

  public Date getCreated() {
    return created;
  }

  public Date getModified() {
    return modified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getWords() {
    return (words == null) ? List.of() : Collections.unmodifiableList(words);
  }

  public int getLength() {
    return (words == null) ? length : words.size();
  }

  public void setLength(int length) {
    if (words != null) {
      throw new IllegalStateException(MUTATION_REQUIRED_MESSAGE);
    }
    this.length = length;
  }

  private synchronized void ensureWordsExists() {
    if (words == null) {
      words = new ArrayList<>();
    }
  }

}
