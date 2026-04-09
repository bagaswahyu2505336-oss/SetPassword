
package setpassword;


public class UserAuth {

    protected String username;
    protected String password;

    public UserAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Method login, nanti bisa dioverride jika perlu
    public boolean login() {
        // Default: return false, harus diimplementasi di subclass
        return false;
    }
}