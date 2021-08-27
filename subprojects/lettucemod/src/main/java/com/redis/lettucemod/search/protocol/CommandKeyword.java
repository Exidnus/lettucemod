package com.redis.lettucemod.search.protocol;

import io.lettuce.core.protocol.ProtocolKeyword;

import java.nio.charset.StandardCharsets;

public enum CommandKeyword implements ProtocolKeyword {

    ADD, MAXTEXTFIELDS, TEMPORARY, NOOFFSETS, NOHL, NOFIELDS, NOFREQS, STOPWORDS, SCHEMA, TEXT, WEIGHT, NUMERIC, GEO,
    PHONETIC, TAG, SEPARATOR, SORTABLE, NOSTEM, NOINDEX, LANGUAGE, PAYLOAD, IF, FIELDS,
    NOCONTENT, VERBATIM, NOSTOPWORDS, FUZZY, WITHPAYLOADS, WITHSORTKEYS, WITHSCORES, MAX, LIMIT, SORTBY, ASC, DESC,
    INCR, DD, LOAD, APPLY, AS, FILTER, GROUPBY, REDUCE, COUNT, COUNT_DISTINCT, COUNT_DISTINCTISH, SUM, MIN, AVG,
    STDDEV, QUANTILE, TOLIST, FIRST_VALUE, RANDOM_SAMPLE, BY, INKEYS, INFIELDS, RETURN, HIGHLIGHT, TAGS, WITHCURSOR,
    MAXIDLE, READ, DEL, ON, PREFIX, LANGUAGE_FIELD, SCORE, SCORE_FIELD, PAYLOAD_FIELD, NOINITIALSCAN, GEOFILTER,
    EXPANDER, SCORER, FRAGS, LEN, SUMMARIZE, SLOP, INORDER, CASESENSITIVE, UNF;

    public final byte[] bytes;

    CommandKeyword() {
        bytes = name().getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
