package com.example.chatgpttest.features.renderer

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MessageContent(
    text: String,
    modifier: Modifier = Modifier
) {
    val segments = remember(text) { parseSegments(text) }

    Column(modifier = modifier) {
        segments.forEach { segment ->
            when (segment) {
                is Segment.PlainText -> MarkdownText(segment.content)
                is Segment.InlineMath -> InlineMathView(segment.content)
                is Segment.BlockMath  -> BlockMathView(segment.content)
                is Segment.CodeBlock  -> CodeBlockView(segment.language, segment.content)
            }
        }
    }
}

sealed class Segment {
    data class PlainText(val content: String) : Segment()
    data class InlineMath(val content: String) : Segment()   // $...$
    data class BlockMath(val content: String) : Segment()    // $$...$$
    data class CodeBlock(val language: String, val content: String) : Segment()
}

fun parseSegments(text: String): List<Segment> {
    val segments = mutableListOf<Segment>()

    val pattern = Regex(
        "(?s)```(\\w*)\\n?(.*?)```" +        // code block
                "|\\$\\$(.*?)\\$\\$" +               // block math
                "|\\$((?:[^$\\n]|\\\\.)+?)\\$",      // inline math
        setOf(RegexOption.MULTILINE)
    )

    var lastEnd = 0

    pattern.findAll(text).forEach { match ->

        if (match.range.first > lastEnd) {
            val plain = text.substring(lastEnd, match.range.first)
            if (plain.isNotEmpty()) segments.add(Segment.PlainText(plain))
        }

        val codeLang = match.groupValues[1]
        val codeContent = match.groupValues[2]
        val blockMath = match.groupValues[3]
        val inlineMath = match.groupValues[4]

        when {
            codeContent.isNotEmpty() || (match.value.startsWith("```")) -> 
                segments.add(Segment.CodeBlock(codeLang, codeContent.trim()))
            blockMath.isNotEmpty() -> 
                segments.add(Segment.BlockMath(blockMath.trim()))
            inlineMath.isNotEmpty() -> 
                segments.add(Segment.InlineMath(inlineMath.trim()))
        }
        
        lastEnd = match.range.last + 1
    }

    if (lastEnd < text.length) {
        val remaining = text.substring(lastEnd)
        if (remaining.isNotEmpty()) segments.add(Segment.PlainText(remaining))
    }

    return segments
}

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val annotated = buildAnnotatedString {
        //    Header(###example)
        val headerPattern = Regex("""^(#{1,6})\s+(.*)$""", RegexOption.MULTILINE)
        //    Bold (**...**) and Italic (*...*)
        val stylePattern = Regex("""\*\*(.*?)\*\*|\*(.*?)\*""")
        
        var cursor = 0

        val processedText = text.trim()

        val lines = processedText.split("\n")
        lines.forEachIndexed { index, line ->
            val headerMatch = headerPattern.matchEntire(line)
            if (headerMatch != null) {
                val level = headerMatch.groupValues[1].length
                val content = headerMatch.groupValues[2]
                val size = when(level) {
                    1 -> 24.sp
                    2 -> 20.sp
                    3 -> 18.sp
                    else -> 16.sp
                }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = size, color = textColor)) {
                    append(content)
                }
            } else {
                // Inline Styles (Bold/Italic)
                var lineCursor = 0
                stylePattern.findAll(line).forEach { m ->
                    if (m.range.first > lineCursor) {
                        withStyle(SpanStyle(color = textColor)) {
                            append(line.substring(lineCursor, m.range.first))
                        }
                    }
                    if (m.groupValues[1].isNotEmpty()) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) { append(m.groupValues[1]) }
                    } else if (m.groupValues[2].isNotEmpty()) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = textColor)) { append(m.groupValues[2]) }
                    }
                    lineCursor = m.range.last + 1
                }
                if (lineCursor < line.length) {
                    withStyle(SpanStyle(color = textColor)) {
                        append(line.substring(lineCursor))
                    }
                }
            }
            if (index < lines.size - 1) append("\n")
        }
    }
    Text(annotated, modifier = modifier, fontSize = 15.sp, lineHeight = 22.sp, color = textColor)
}


@Composable
fun BlockMathView(latex: String, modifier: Modifier = Modifier) {
    KatexWebView(
        latex = latex,
        displayMode = true,
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp, max = 200.dp)
    )
}

@Composable
fun InlineMathView(latex: String, modifier: Modifier = Modifier) {
    KatexWebView(
        latex = latex,
        displayMode = false,
        modifier = modifier.heightIn(min = 32.dp, max = 80.dp)
    )
}

@Composable
fun KatexWebView(
    latex: String,
    displayMode: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) "white" else "black"

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.domStorageEnabled = true
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
            }
        },
        update = { webView ->
            val escaped = latex
                .replace("\\", "\\\\")
                .replace("`", "\\`")
                .replace("$", "\\$")

            val html = """
        <!DOCTYPE html><html><head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
        <script src="file:///android_asset/katex/katex.min.js"></script>
        <style>
          body { margin:0; padding:4px; background:transparent; color:${textColor}; font-family: sans-serif; }
          #f { font-size: 1.1em; overflow-x: auto; }
        </style></head>
        <body><div id="f"></div>
        <script>
          window.onload = function() {
            try {
              katex.render(`${escaped}`, document.getElementById("f"), {
                throwOnError: false,
                displayMode: ${displayMode}
              });
            } catch(e) {
              document.getElementById("f").innerHTML = '<span style="color:red">' + e.message + '</span>';
            }
          };
        </script></body></html>
    """.trimIndent()

            webView.loadDataWithBaseURL(
                "file:///android_asset/", html, "text/html", "UTF-8", null
            )
        }
    )
}

@Composable
fun CodeBlockView(language: String, code: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E1E1E))
            .padding(12.dp)
    ) {
        if (language.isNotEmpty()) {
            Text(
                language,
                color = Color(0xFF858585),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Text(
            code,
            color = Color(0xFFD4D4D4),
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}