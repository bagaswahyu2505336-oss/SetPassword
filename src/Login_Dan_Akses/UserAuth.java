
package Login_Dan_Akses;


public class UserAuth {

    protected String username;
    protected String password;

    public UserAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login() {
        return false;
    }
}