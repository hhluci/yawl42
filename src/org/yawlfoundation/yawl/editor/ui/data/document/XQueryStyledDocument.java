/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.data.document;

import org.yawlfoundation.yawl.editor.ui.data.Validity;
import org.yawlfoundation.yawl.editor.ui.data.editorpane.ValidityEditorPane;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.ExpressionMatcher;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.XQueryEvaluator;
import org.yawlfoundation.yawl.elements.predicate.PredicateEvaluatorCache;

import java.util.ArrayList;
import java.util.List;


public class XQueryStyledDocument extends AbstractXMLStyledDocument {

    private String preEditorText;
    private String postEditorText;
    private List<String> errorList;
    private ExpressionMatcher externalMatcher;
    private ExpressionMatcher timerMatcher;

    public XQueryStyledDocument(ValidityEditorPane editor) {
        super(editor);
        setDocumentFilter(new IgnoreBadCharactersFilter());
        setPreAndPostEditorText("","");
        errorList = new ArrayList<String>();
        externalMatcher = new ExpressionMatcher("#external:\\w+\\s*:\\w+\\s*");
        timerMatcher = new ExpressionMatcher("timer\\(\\w+\\)\\s*!?=\\s*" +
                               "'(dormant|active|closed|expired)'");

    }

    // only strict encodes required
    public static String encodeEscapes(String s) {
        if (s == null) return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public void setPreAndPostEditorText(String preEditorText, String postEditorText) {
        this.preEditorText = preEditorText;
        this.postEditorText = postEditorText;
    }

    public void checkValidity() {
        errorList.clear();
        if (isValidating()) {
            String text = getEditor().getText();
            if (text.equals("")) {
                errorList.add("Query required");
                setContentValidity(Validity.INVALID);
                return;
            }

            // replace external predicate references with dummy values for validation
            text = substituteExternals(text);

            // dereference reserved XML chars when not a splitter or joiner query
//            if (!text.startsWith("for $")) text = encodeEscapes(text);

            errorList.addAll(XQueryEvaluator.compile(text));
            setContentValidity(errorList.isEmpty() ? Validity.VALID : Validity.INVALID);
        }
    }

    private String substituteExternals(String binding) {
        binding = externalMatcher.replaceAll(binding, "");
        binding = timerMatcher.replaceAll(binding, "false");
        if (PredicateEvaluatorCache.accept(binding)) {
            binding = PredicateEvaluatorCache.substitute(binding);
        }
        return binding;
    }

    public List<String> getProblemList() {
        return errorList;
    }
}
