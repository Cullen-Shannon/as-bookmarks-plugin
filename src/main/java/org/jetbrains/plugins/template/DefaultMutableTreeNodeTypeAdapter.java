package org.jetbrains.plugins.template;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.plugins.template.domain.MyMenuItem;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/*
    Copied from SO for serializing/deserializing tree and JSON
 */
public class DefaultMutableTreeNodeTypeAdapter extends TypeAdapter<DefaultMutableTreeNode> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

        @Override
        @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == DefaultMutableTreeNode.class) {
                return (TypeAdapter<T>) new DefaultMutableTreeNodeTypeAdapter(gson);
            }
            return null;
        }
    };

    private final Gson gson;

    private DefaultMutableTreeNodeTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, DefaultMutableTreeNode node) throws IOException {
        out.beginObject();
        out.name("allowsChildren");
        out.value(node.getAllowsChildren());
        out.name("userObject");
        gson.toJson(node.getUserObject(), MyMenuItem.class, out);
        if (node.getChildCount() > 0) {
            out.name("children");
            gson.toJson(Collections.list(node.children()), List.class, out); // recursion!
        }
        // No need to write node.getParent(), it would lead to infinite recursion.
        out.endObject();
    }

    @Override
    public DefaultMutableTreeNode read(JsonReader in) throws IOException {
        in.beginObject();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "allowsChildren":
                    node.setAllowsChildren(in.nextBoolean());
                    break;
                case "userObject":
                    node.setUserObject(gson.fromJson(in, MyMenuItem.class));
                    break;
                case "children":
                    in.beginArray();
                    while (in.hasNext()) {
                        node.add(read(in)); // recursion!
                        // this did also set the parent of the child-node
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return node;
    }
}

