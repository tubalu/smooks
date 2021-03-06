/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.javabean.dynamic.visitor;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Namespace Reaper.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NamespaceReaper implements DOMVisitBefore {

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        Map<String, String> namespacePrefixMappings = getNamespacePrefixMappings(executionContext);
        NamedNodeMap attributes = element.getAttributes();
        int attributeCount = attributes.getLength();

        for(int i = 0; i < attributeCount; i++) {
            Attr attr = (Attr) attributes.item(i);

            if(XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                String uri = attr.getValue();
                QName attrQName = SAXUtil.toQName(uri, attr.getLocalName(), attr.getNodeName());

                addMapping(namespacePrefixMappings, uri, attrQName.getLocalPart());
            }
        }
    }

    private void addMapping(Map<String, String> namespacePrefixMappings, String uri, String prefix) {
        if(uri != null && prefix != null && !namespacePrefixMappings.containsKey(uri)) {
            namespacePrefixMappings.put(uri, prefix);
        }
    }

    public static Map<String, String> getNamespacePrefixMappings(ExecutionContext executionContext) {
        Map<String, String> namespacePrefixMappings = (Map<String, String>) executionContext.getAttribute(NamespaceReaper.class);

        if(namespacePrefixMappings == null) {
            namespacePrefixMappings = new LinkedHashMap<String, String>();
            executionContext.setAttribute(NamespaceReaper.class, namespacePrefixMappings);
        }

        return namespacePrefixMappings;
    }
}
