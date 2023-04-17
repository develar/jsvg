package com.github.weisj.jsvg.parser;

import com.github.weisj.jsvg.nodes.*;
import com.github.weisj.jsvg.nodes.animation.Animate;
import com.github.weisj.jsvg.nodes.animation.AnimateTransform;
import com.github.weisj.jsvg.nodes.animation.Set;
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
    // do not use method reference - it will load the class
    // Foo::new loads the class Foo eagerly, while () -> new Foo() does that only when actually invoked
    public static @NotNull Map<String, Supplier<SVGNode>> createNodeConstructorMap(Map<String, Supplier<SVGNode>> map) {
        map.put(Anchor.TAG, () -> new Anchor());
        map.put(Circle.TAG, () -> new Circle());
        map.put(ClipPath.TAG, () -> new ClipPath());
        map.put(Defs.TAG, () -> new Defs());
        map.put(Desc.TAG, () -> new Desc());
        map.put(Ellipse.TAG, () -> new Ellipse());
        map.put(Group.TAG, () -> new Group());
        map.put(Image.TAG, () -> new Image());
        map.put(Line.TAG, () -> new Line());
        map.put(LinearGradient.TAG, () -> new LinearGradient());
        map.put(Marker.TAG, () -> new Marker());
        map.put(Mask.TAG, () -> new Mask());
        map.put(MeshGradient.TAG, () -> new MeshGradient());
        map.put(MeshPatch.TAG, () -> new MeshPatch());
        map.put(MeshRow.TAG, () -> new MeshRow());
        map.put(Metadata.TAG, () -> new Metadata());
        map.put(Path.TAG, () -> new Path());
        map.put(Pattern.TAG, () -> new Pattern());
        map.put(Polygon.TAG, () -> new Polygon());
        map.put(Polyline.TAG, () -> new Polyline());
        map.put(RadialGradient.TAG, () -> new RadialGradient());
        map.put(Rect.TAG, () -> new Rect());
        map.put(SVG.TAG, () -> new SVG());
        map.put(SolidColor.TAG, () -> new SolidColor());
        map.put(Stop.TAG, () -> new Stop());
        map.put(Style.TAG, () -> new Style());
        map.put(Symbol.TAG, () -> new Symbol());
        map.put(Text.TAG, () -> new Text());
        map.put(TextPath.TAG, () -> new TextPath());
        map.put(TextSpan.TAG, () -> new TextSpan());
        map.put(Title.TAG, () -> new Title());
        map.put(Use.TAG, () -> new Use());
        map.put(View.TAG, () -> new View());

        map.put(Filter.TAG, () -> new Filter());
        map.put(FeBlend.TAG, () -> new FeBlend());
        map.put(FeColorMatrix.TAG, () -> new FeColorMatrix());
        map.put(FeDisplacementMap.TAG, () -> new FeDisplacementMap());
        map.put(FeFlood.TAG, () -> new FeFlood());
        map.put(FeGaussianBlur.TAG, () -> new FeGaussianBlur());
        map.put(FeMerge.TAG, () -> new FeMerge());
        map.put(FeMergeNode.TAG, () -> new FeMergeNode());
        map.put(FeTurbulence.TAG, () -> new FeTurbulence());
        map.put(FeOffset.TAG, () -> new FeOffset());

        map.put(Animate.TAG, () -> new Animate());
        map.put(AnimateTransform.TAG, () -> new AnimateTransform());
        map.put(Set.TAG, () -> new Set());

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
        map.put("feMorphology", () -> new DummyFilterPrimitive("feMorphology"));
        map.put("feSpecularLighting", () -> new DummyFilterPrimitive("feSpecularLighting"));
        map.put("feTile", () -> new DummyFilterPrimitive("feTile"));

        return map;
    }
}
