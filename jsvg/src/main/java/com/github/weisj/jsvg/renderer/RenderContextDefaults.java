package com.github.weisj.jsvg.renderer;

import com.github.weisj.jsvg.attributes.font.MeasurableFontSpec;
import com.github.weisj.jsvg.attributes.paint.AwtSVGPaint;
import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.attributes.paint.SVGPaint;
import org.jetbrains.annotations.NotNull;

final class RenderContextDefaults {
    public static final MeasurableFontSpec FONT_SPEC = MeasurableFontSpec.createDefault();
    static final AwtSVGPaint DEFAULT_PAINT = new AwtSVGPaint(PaintParser.DEFAULT_COLOR);
    static final PaintContext DEFAULT_CONTEXT = createDefault();

    static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, null, null);

    private static @NotNull PaintContext createDefault() {
        return new PaintContext(
            DEFAULT_PAINT,
            DEFAULT_PAINT, 1,
            SVGPaint.NONE, 1, 1,
            StrokeContext.createDefault());
    }
}
