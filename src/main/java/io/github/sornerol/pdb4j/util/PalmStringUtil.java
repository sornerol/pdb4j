package io.github.sornerol.pdb4j.util;

/**
 * Helper methods for dealing with Palm OS string encoding
 */
public class PalmStringUtil {

    /**
     * Convert a string from Palm OS encoding to Unicode
     * @param string String encoded in Palm OS character encoding
     * @return Unicode string
     */
    public static String palmToUnicode(String string) {
        return string.replace('\u0018', '\u2026') // Horizontal ellipsis
                .replace('\u0019', '\u2007') // Figure space
                .replace('\u0080', '\u20AC') // Euro sign
                .replace('\u0082', '\u201A') // Single low-9 quotation mark
                .replace('\u0083', '\u0192') // Latin small letter F with hook
                .replace('\u0084', '\u201E') // Double low-9 quotation mark
                .replace('\u0085', '\u2026') // Horizontal ellipsis
                .replace('\u0086', '\u2020') // Dagger
                .replace('\u0087', '\u2021') // Double dagger
                .replace('\u0088', '\u0302') // Combining circumflex accent
                .replace('\u0089', '\u2030') // Per mille sign
                .replace('\u008A', '\u0160') // Latin capital letter S with caron
                .replace('\u008B', '\u2039') // Single left-pointing angle quotation mark
                .replace('\u008C', '\u0152') // Latin capital ligature oe
                .replace('\u008D', '\u2662') // White diamond suit
                .replace('\u008E', '\u2663') // Black club suit
                .replace('\u008F', '\u2661') // White heart suit
                .replace('\u0090', '\u2660') // Black spade suit
                .replace('\u0091', '\u2018') // Left single quotation mark
                .replace('\u0092', '\u2019') // Right single quotation mark
                .replace('\u0093', '\u201C') // Left double quotation mark
                .replace('\u0094', '\u201D') // Right double quotation mark
                .replace('\u0095', '\u2219') // Bullet operator
                .replace('\u0096', '\u2011') // Non-breaking hyphen
                .replace('\u0097', '\u2012') // Figure dash
                .replace('\u0098', '\u0303') // Combining tilde
                .replace('\u0099', '\u2122') // Trade mark sign
                .replace('\u009A', '\u0161') // Latin small letter s with caron
                .replace('\u009B', '\u203A') // Single right-pointing angle quotation mark
                .replace('\u009C', '\u0153') // Latin small ligature oe
                .replace('\u009F', '\u0178'); // Latin capital letter Y with diaeresis
    }

    /**
     * Convert a string from Unicode to Palm OS encoding
     * @param string String encoded in Unicode
     * @return Palm OS encoded string
     */
    public static String unicodeToPalm(String string) {
        return string.replace('\u2026', '\u0018') // Horizontal ellipsis
                .replace('\u2007', '\u0019') // Figure space
                .replace('\u20AC', '\u0080') // Euro sign
                .replace('\u201A', '\u0082') // Single low-9 quotation mark
                .replace('\u0192', '\u0083') // Latin small letter F with hook
                .replace('\u201E', '\u0084') // Double low-9 quotation mark
                .replace('\u2020', '\u0086') // Dagger
                .replace('\u2021', '\u0087') // Double dagger
                .replace('\u0302', '\u0088') // Combining circumflex accent
                .replace('\u2030', '\u0089') // Per mille sign
                .replace('\u0160', '\u008A') // Latin capital letter S with caron
                .replace('\u2039', '\u008B') // Single left-pointing angle quotation mark
                .replace('\u0152', '\u008C') // Latin capital ligature oe
                .replace('\u2662', '\u008D') // White diamond suit
                .replace('\u2663', '\u008E') // Black club suit
                .replace('\u2661', '\u008F') // White heart suit
                .replace('\u2660', '\u0090') // Black spade suit
                .replace('\u2018', '\u0091') // Left single quotation mark
                .replace('\u2019', '\u0092') // Right single quotation mark
                .replace('\u201C', '\u0093') // Left double quotation mark
                .replace('\u201D', '\u0094') // Right double quotation mark
                .replace('\u2219', '\u0095') // Bullet operator
                .replace('\u2011', '\u0096') // Non-breaking hyphen
                .replace('\u2012', '\u0097') // Figure dash
                .replace('\u0303', '\u0098') // Combining tilde
                .replace('\u2122', '\u0099') // Trade mark sign
                .replace('\u0161', '\u009A') // Latin small letter s with caron
                .replace('\u203A', '\u009B') // Single right-pointing angle quotation mark
                .replace('\u0153', '\u009C') // Latin small ligature oe
                .replace('\u0178', '\u009F'); // Latin capital letter Y with diaeresis
    }
}
