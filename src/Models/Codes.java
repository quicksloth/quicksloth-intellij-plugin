package Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class Codes {

    @SerializedName("code")
    private String codeText;

    private Float score;

    @SerializedName("source_link")
    private String sourceLink;

    public Codes(String codeText, Float score, String sourceLink) {
        this.codeText = codeText;
        this.score = score;
        this.sourceLink = sourceLink;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }
}
