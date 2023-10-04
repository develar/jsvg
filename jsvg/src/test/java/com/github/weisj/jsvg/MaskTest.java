/*
 * MIT License
 *
 * Copyright (c) 2021-2023 Jannis Weis
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
package com.github.weisj.jsvg;

import static com.github.weisj.jsvg.ReferenceTest.ReferenceTestResult.SUCCESS;
import static com.github.weisj.jsvg.ReferenceTest.compareImages;
import static com.github.weisj.jsvg.ReferenceTest.render;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class MaskTest {

    @Test
    void testMaskUnits() {
        assertEquals(SUCCESS, compareImages("mask/maskUnits.svg"));
    }

    @Test
    void testMaskContentUnits() {
        assertEquals(SUCCESS, compareImages("mask/maskContentUnits.svg"));
    }

    @Test
    void testTranslucentMask() {
        assertEquals(SUCCESS, compareImages("mask/translucentMask.svg"));
    }

    @Disabled("Overlapping elements currently may bleed through the mask")
    @Test
    void testOverlapping() {
        assertEquals(SUCCESS, compareImages("mask/overlapping.svg", 0.1));
    }

    @Test
    void referenceTests() {
        assertEquals(SUCCESS, compareImages("mask/mask1.svg"));
        assertEquals(SUCCESS, compareImages("mask/mask2.svg"));
        // [Note: Flaky] assertEquals(SUCCESS, compareImages("mask/chromeLogo.svg"));
        assertEquals(SUCCESS, compareImages("mask/classIcon.svg"));
        assertEquals(SUCCESS, compareImages("mask/complexTransform_bug32.svg"));
    }

    @Test
    void emptyGroupReportsCorrectSize() {
        assertDoesNotThrow(() -> render("mask/empty_group_issue_48.svg"));
    }
}
