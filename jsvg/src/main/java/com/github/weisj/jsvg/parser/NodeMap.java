package com.github.weisj.jsvg.parser;

import com.github.weisj.jsvg.nodes.*;
import com.github.weisj.jsvg.nodes.filter.*;
import com.github.weisj.jsvg.nodes.mesh.MeshGradient;
import com.github.weisj.jsvg.nodes.mesh.MeshPatch;
import com.github.weisj.jsvg.nodes.mesh.MeshRow;
import com.github.weisj.jsvg.nodes.text.Text;
import com.github.weisj.jsvg.nodes.text.TextPath;
import com.github.weisj.jsvg.nodes.text.TextSpan;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public final class NodeMap {
    public static @NotNull Map<String, Supplier<SVGNode>> createNodeConstructorMap(Map<String, Supplier<SVGNode>> map) {
        map.put(Anchor.TAG, Anchor::new);
        map.put(Circle.TAG, Circle::new);
        map.put(ClipPath.TAG, ClipPath::new);
        map.put(Defs.TAG, Defs::new);
        map.put(Desc.TAG, Desc::new);
        map.put(Ellipse.TAG, Ellipse::new);
        map.put(FeColorMatrix.TAG, FeColorMatrix::new);
        map.put(FeDisplacementMap.TAG, FeDisplacementMap::new);
        map.put(FeGaussianBlur.TAG, FeGaussianBlur::new);
        map.put(FeTurbulence.TAG, FeTurbulence::new);
        map.put(Filter.TAG, Filter::new);
        map.put(Group.TAG, Group::new);
        map.put(Image.TAG, Image::new);
        map.put(Line.TAG, Line::new);
        map.put(LinearGradient.TAG, LinearGradient::new);
        map.put(Marker.TAG, Marker::new);
        map.put(Mask.TAG, Mask::new);
        map.put(MeshGradient.TAG, MeshGradient::new);
        map.put(MeshPatch.TAG, MeshPatch::new);
        map.put(MeshRow.TAG, MeshRow::new);
        map.put(Metadata.TAG, Metadata::new);
        map.put(Path.TAG, Path::new);
        map.put(Pattern.TAG, Pattern::new);
        map.put(Polygon.TAG, Polygon::new);
        map.put(Polyline.TAG, Polyline::new);
        map.put(RadialGradient.TAG, RadialGradient::new);
        map.put(Rect.TAG, Rect::new);
        map.put(SVG.TAG, SVG::new);
        map.put(SolidColor.TAG, SolidColor::new);
        map.put(Stop.TAG, Stop::new);
        map.put(Style.TAG, Style::new);
        map.put(Symbol.TAG, Symbol::new);
        map.put(Text.TAG, Text::new);
        map.put(TextPath.TAG, TextPath::new);
        map.put(TextSpan.TAG, TextSpan::new);
        map.put(Title.TAG, Title::new);
        map.put(Use.TAG, Use::new);
        map.put(View.TAG, View::new);

        map.put(FeBlend.TAG, FeBlend::new);
        map.put(FeFlood.TAG, FeFlood::new);

        map.put("feComponentTransfer", () -> new DummyFilterPrimitive("feComponentTransfer"));
        map.put("feComposite", () -> new DummyFilterPrimitive("feComposite"));
        map.put("feConvolveMatrix", () -> new DummyFilterPrimitive("feConvolveMatrix"));
        map.put("feDiffuseLightning", () -> new DummyFilterPrimitive("feDiffuseLightning"));
        map.put("feDisplacementMap", () -> new DummyFilterPrimitive("feDisplacementMap"));
        map.put("feDropShadow", () -> new DummyFilterPrimitive("feDropShadow"));
        map.put("feFuncA", () -> new DummyFilterPrimitive("feFuncA"));
        map.put("feFuncB", () -> new DummyFilterPrimitive("feFuncB"));
        map.put("feFuncG", () -> new DummyFilterPrimitive("feFuncG"));
        map.put("feFuncR", () -> new DummyFilterPrimitive("feFuncR"));
        map.put("feImage", () -> new DummyFilterPrimitive("feImage"));
        map.put("feMerge", () -> new DummyFilterPrimitive("feMerge"));
        map.put("feMorphology", () -> new DummyFilterPrimitive("feMorphology"));
        map.put("feOffset", () -> new DummyFilterPrimitive("feOffset"));
        map.put("feSpecularLighting", () -> new DummyFilterPrimitive("feSpecularLighting"));
        map.put("feTile", () -> new DummyFilterPrimitive("feTile"));

        return map;
    }
}
