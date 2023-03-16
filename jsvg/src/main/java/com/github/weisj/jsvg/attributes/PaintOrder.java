/*
 * MIT License
 *
 * Copyright (c) 2022 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.weisj.jsvg.attributes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.parser.AttributeNode;

import java.util.List;

public final class PaintOrder {

    private static final PaintOrder NORMAL = new PaintOrder(Phase.FILL, Phase.STROKE, Phase.MARKERS);

    public enum Phase {
        FILL,
        STROKE,
        MARKERS
    }

    private final Phase @NotNull [] phases;

    private PaintOrder(Phase @NotNull ... phases) {
        this.phases = phases;
    }

    public Phase @NotNull [] phases() {
        return phases;
    }

    public static @NotNull PaintOrder parse(@NotNull AttributeNode attributeNode) {
        @Nullable String value = attributeNode.getValue("paint-order");
        @NotNull AttributeParser parser = attributeNode.parser();

        if (value == null || "normal".equals(value)) return NORMAL;

        List<String> rawPhases = parser.parseStringList(value, false);
        Phase[] phases = new Phase[3];
        int length = Math.min(phases.length, rawPhases.size());
        int i = 0;
        while (i < length) {
            phases[i] = parser.parseEnum(rawPhases.get(i), Phase.class);
            if (phases[i] != null) i++;
        }
        while (i < 3) {
            // Fill up with normal order
            phases[i] = findNextInNormalOrder(phases, i);
            i++;
        }
        return new PaintOrder(phases);
    }

    private static @NotNull Phase findNextInNormalOrder(Phase @NotNull [] phases, int maxIndex) {
        for (Phase phase : NORMAL.phases()) {
            boolean found = false;
            for (int i = 0; i < maxIndex; i++) {
                if (phases[i] == phase) {
                    found = true;
                    break;
                }
            }
            if (!found) return phase;
        }
        throw new IllegalStateException();
    }
}
