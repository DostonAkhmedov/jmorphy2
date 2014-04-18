package net.uaprom.jmorphy2.test;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import net.uaprom.jmorphy2.FileLoader;
import net.uaprom.jmorphy2.MorphAnalyzer;


public class _BaseTestCase {
    protected MorphAnalyzer morph;
    private boolean initialized = false;

    public void initMorphAnalyzer() throws IOException {
        if (initialized) {
            return;
        }

        final String dictPath = "/pymorphy2_dicts";
        Map<Character,String> replaceChars = new HashMap<Character,String>();
        replaceChars.put('е', "ё");
        morph = new MorphAnalyzer(new FileLoader() {
                @Override
                public InputStream getStream(String filename) throws IOException {
                    return getClass().getResourceAsStream((new File(dictPath, filename)).getPath());
                }
            },
            replaceChars,
            0);
        initialized = true;
    }
}
