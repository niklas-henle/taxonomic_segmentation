package models.records;

import utils.DatabaseConnector;

import java.sql.SQLException;

public record Alignment(String readId, String sseqid, int qstart, int qend, float eval, float bitscore) {

/**
      @Override public String sseqid() {
          try {
          DatabaseConnector db = new DatabaseConnector("data/database/megan-map-Feb2022.db");
          String gtdb = db.queryAccessionNumber(this.sseqid);
          return gtdb != null ? gtdb : this.sseqid;
          } catch (SQLException e) {
          throw new RuntimeException(e);
          }
      }
      **/

    @Override
    public boolean equals(Object obj) {

        return ((Alignment) obj).sseqid.equals(this.sseqid);
    }

    public boolean contains(int i) {
        return i >= qstart && i <= qend;
    }

}

