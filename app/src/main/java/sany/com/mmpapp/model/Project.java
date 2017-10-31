package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 16-12-2.
 */
public class Project {
    private String  pi_name;
    private String  pi_id;
    private boolean isLoading=false;

    public Project(String pi_name, String pi_id) {
        this.pi_name = pi_name;
        this.pi_id = pi_id;
    }

    public String getPi_name() {
        return pi_name;
    }

    public void setPi_name(String pi_name) {
        this.pi_name = pi_name;
    }

    public String getPi_id() {
        return pi_id;
    }

    public void setPi_id(String pi_id) {
        this.pi_id = pi_id;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
}
