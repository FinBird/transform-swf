/*
 * Context.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.coder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class Context {

    /** TODO(doc). */
    public static final int VERSION = 1;
    /** TODO(doc). */
    public static final int TYPE = 2;
    /** TODO(doc). */
    public static final int TRANSPARENT = 3;
    /** TODO(doc). */
    public static final int WIDE_CODES = 4;
    /** TODO(doc). */
    public static final int ARRAY_EXTENDED = 8;
    /** TODO(doc). */
    public static final int POSTSCRIPT = 9;
    /** TODO(doc). */
    public static final int SCALING_STROKE = 10;
    /** TODO(doc). */
    public static final int FILL_SIZE = 11;
    /** TODO(doc). */
    public static final int LINE_SIZE = 12;
    /** TODO(doc). */
    public static final int ADVANCE_SIZE = 13;
    /** TODO(doc). */
    public static final int GLYPH_SIZE = 14;
    /** TODO(doc). */
    public static final int SHAPE_SIZE = 15;
    /** TODO(doc). */
    public static final int LAST = 16;

    /** The character encoding used for strings. */
    private String encoding;
    /** The registry containing the objects that perform the decoding. */
    private DecoderRegistry registry;
    /** A table of variables used to pass information between objects. */
    private Map<Integer, Integer> variables;

    /**
     * Create a Context object.
     */
    public Context() {
        variables = new LinkedHashMap<Integer, Integer>();
    }

    /**
     * Get character encoding scheme used when encoding or decoding strings.
     *
     * @return the character encoding used for strings.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the character encoding scheme used when encoding or decoding
     * strings.
     *
     * If the character set encoding is not supported by the Java environment
     * then an UnsupportedCharsetException will be thrown. If the character set
     * cannot be identified then an IllegalCharsetNameException will be thrown.
     *
     * @param charSet
     *            the name of the character set used to encode strings.
     */
    public void setEncoding(final String charSet) {
        if (!Charset.isSupported(charSet)) {
            throw new UnsupportedCharsetException(charSet);
        }
        encoding = charSet;
    }

    /**
     * Calculates the length of a string when encoded using the specified
     * character set.
     *
     * @param string
     *            the string to be encoded.
     *
     * @return the number of bytes required to encode the string plus 1 for a
     *         terminating null character.
     */

    public int strlen(final String string) {
        try {
            return string.getBytes(encoding).length + 1;
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Get the registry containing the decoders for different types of objects.
     * @return the decoder registry.
     */
    public DecoderRegistry getRegistry() {
        return registry;
    }

    /**
     * Set the registry containing the decoders for different types of objects.
     * @param decoderRegistry the registry containing decoder objects.
     */
    public void setRegistry(final DecoderRegistry decoderRegistry) {
        registry = decoderRegistry;
    }

    /**
     * Get the table of variables used to exchange information between objects.
     * @return the variable table.
     */
    public Map<Integer, Integer> getVariables() {
        return variables;
    }

    /**
     * Set the table of variables used to exchange information between objects.
     * @param map the variable table.
     */
    public void setVariables(final Map<Integer, Integer> map) {
        variables = map;
    }

    /**
     * Set a variable.
     * @param key the name of the variable.
     * @param value the variable value.
     * @return this object.
     */
    public Context put(final Integer key, final Integer value) {
        variables.put(key, value);
        return this;
    }
}
