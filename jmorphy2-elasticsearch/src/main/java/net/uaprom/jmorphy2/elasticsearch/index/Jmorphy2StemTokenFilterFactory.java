package net.uaprom.jmorphy2.elasticsearch.index;

import java.util.Set;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.elasticsearch.index.analysis.AnalysisSettingsRequired;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

import net.uaprom.jmorphy2.MorphAnalyzer;
import net.uaprom.jmorphy2.lucene.Jmorphy2StemFilter;
import static net.uaprom.jmorphy2.lucene.Jmorphy2StemFilterFactory.parseTags;
import net.uaprom.jmorphy2.elasticsearch.indices.Jmorphy2Analysis;


@AnalysisSettingsRequired
public class Jmorphy2StemTokenFilterFactory extends AbstractTokenFilterFactory {
    private final MorphAnalyzer morph;

    private final List<Set<String>> includeTags;
    private final List<Set<String>> excludeTags;

    @Inject
    public Jmorphy2StemTokenFilterFactory(Index index,
                                          @IndexSettings Settings indexSettings,
                                          @Assisted String name,
                                          @Assisted Settings settings,
                                          Jmorphy2Analysis jmorphy2Service) {
        super(index, indexSettings, name, settings);

        String dictPath = settings.get("name");
        morph = jmorphy2Service.getMorphAnalyzer(dictPath);

        includeTags = parseTags(settings.get("include_tags"));
        excludeTags = parseTags(settings.get("exclude_tags"));
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new Jmorphy2StemFilter(tokenStream, morph, includeTags, excludeTags);
    }
}
