package Models;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class RequestCode implements Serializable {
    private String query;
    private String language;
    private List<String> libs;
    private List<String> comments;

    public RequestCode(String query, String language, List<String> libs, List<String> comments) {
        this.query = query;
        this.language = language;
        this.libs = libs;
        this.comments = comments;
    }

    public RequestCode() {
        this.query = "";
        this.language = "";
        this.libs = Collections.emptyList();
        this.comments = Collections.emptyList();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getLibs() {
        return libs;
    }

    public void setLibs(List<String> libs) {
        this.libs = libs;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
