package Models;

import java.util.List;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class RecommendedCodes {

    private List<Codes> codes;

    public RecommendedCodes(List<Codes> codes) {
        this.codes = codes;
    }

    public List<Codes> getCodes() {
        return codes;
    }

    public void setCodes(List<Codes> codes) {
        this.codes = codes;
    }
}
