/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/** Helper for `PlayException`. */
public class PlayException extends UsefulException {

  /** Statically compiled Pattern for splitting lines. */
  private static final Pattern SPLIT_LINES = Pattern.compile("\\r?\\n");

  private final AtomicLong generator = new AtomicLong(System.currentTimeMillis());

  /** Generates a new unique exception ID. */
  private String nextId() {
    return java.lang.Long.toString(generator.incrementAndGet(), 26);
  }

  public PlayException(String title, String description, Throwable cause) {
    super(title + "[" + description + "]", cause);
    this.title = title;
    this.description = description;
    this.id = nextId();
    this.cause = cause;
  }

  public PlayException(String title, String description) {
    super(title + "[" + description + "]");
    this.title = title;
    this.description = description;
    this.id = nextId();
    this.cause = null;
  }

  /** Adds source attachment to a Play exception. */
  public abstract static class ExceptionSource extends PlayException {

    public ExceptionSource(String title, String description, Throwable cause) {
      super(title, description, cause);
    }

    public ExceptionSource(String title, String description) {
      super(title, description);
    }

    /**
     * Error line number, if defined.
     *
     * @return Error line number, if defined.
     */
    public abstract Integer line();

    /**
     * Column position, if defined.
     *
     * @return Column position, if defined.
     */
    public abstract Integer position();

    /**
     * @return Input stream used to read the source content.
     *     <p>Input stream used to read the source content.
     */
    public abstract String input();

    /**
     * The source file name if defined.
     *
     * @return The source file name if defined.
     */
    public abstract String sourceName();

    /**
     * Extracts interesting lines to be displayed to the user.
     *
     * @param border number of lines to use as a border
     * @return the extracted lines
     */
    public InterestingLines interestingLines(int border) {
      try {
        if (input() == null || line() == null) {
          return null;
        }

        String[] lines = SPLIT_LINES.split(input(), 0);
        int firstLine = Math.max(0, line() - 1 - border);
        int lastLine = Math.min(lines.length - 1, line() - 1 + border);
        List<String> focusOn = new ArrayList<String>();
        for (int i = firstLine; i <= lastLine; i++) {
          focusOn.add(lines[i]);
        }
        return new InterestingLines(
            firstLine + 1, focusOn.toArray(new String[focusOn.size()]), line() - firstLine - 1);
      } catch (Throwable e) {
        e.printStackTrace();
        return null;
      }
    }

    public String toString() {
      return super.toString() + " in " + sourceName() + ":" + line();
    }
  }

  /** Adds any attachment to a Play exception. */
  public abstract static class ExceptionAttachment extends PlayException {

    public ExceptionAttachment(String title, String description, Throwable cause) {
      super(title, description, cause);
    }

    public ExceptionAttachment(String title, String description) {
      super(title, description);
    }

    /**
     * Content title.
     *
     * @return content title.
     */
    public abstract String subTitle();

    /**
     * Content to be displayed.
     *
     * @return content to be displayed.
     */
    public abstract String content();
  }

  /** Adds a rich HTML description to a Play exception. */
  public abstract static class RichDescription extends ExceptionAttachment {

    public RichDescription(String title, String description, Throwable cause) {
      super(title, description, cause);
    }

    public RichDescription(String title, String description) {
      super(title, description);
    }

    /**
     * The new description formatted as HTML.
     *
     * @return the new description formatted as HTML.
     */
    public abstract String htmlDescription();
  }

  public static class InterestingLines {

    public final int firstLine;
    public final int errorLine;
    public final String[] focus;

    public InterestingLines(int firstLine, String[] focus, int errorLine) {
      this.firstLine = firstLine;
      this.errorLine = errorLine;
      this.focus = focus;
    }
  }
}
