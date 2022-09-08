package models;

import utils.DatabaseConnector;
import java.sql.SQLException;

public record Alignment(String sseqid, int qstart, int qend, float eval, float bitscore) {

/**
      @Override public String sseqid() {
          try {
          DatabaseConnector db = new DatabaseConnector("data/database/megan-map-Feb2022.db");
          return db.queryAccessionNumber(sseqid);
          } catch (SQLException e) {
          throw new RuntimeException(e);
          }
      }
*/

    @Override
    public boolean equals(Object obj) {

        return ((Alignment) obj).sseqid.equals(this.sseqid);
    }

}

