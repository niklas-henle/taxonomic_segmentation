package models.records;


/**
 * Alignment record collecting information about the alignment contained in the input file
 * @param readId readid
 * @param sseqid taxonomic identification
 * @param qstart start of the alignment
 * @param qend end of the alignment
 * @param eval e-value
 * @param bitscore bitscore
 */

public record Alignment(String readId, String sseqid, int qstart, int qend, float eval, float bitscore) {


    /**
     * Override of the equals function.
     * Equality of an alignment should be based on the sseqid.
     * @param obj   the reference object with which to compare.
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {

        return ((Alignment) obj).sseqid.equals(this.sseqid);
    }

    /**
     * Checks if the records contains an index
     * @param i index to check for
     * @return boolean
     */
    public boolean contains(int i) {
        return i >= qstart && i <= qend;
    }

}

