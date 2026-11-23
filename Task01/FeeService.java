import java.sql.*;
public class FeeService {
    private final String url, user, password;
    public FeeService(String url, String user, String password) {
        this.url=url; this.user=user; this.password=password;
    }
    public String pay(String regNo, String amountText) throws Exception {
        if (regNo == null || regNo.trim().isEmpty()) throw new IllegalArgumentException("Registration number is required.");
        double amount;
        try { amount=Double.parseDouble(amountText); }
        catch (Exception ex) { throw new IllegalArgumentException("Enter a valid amount."); }
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        String sql="INSERT INTO fees(reg_no, amount) VALUES (?, ?)";
        try (Connection c=DriverManager.getConnection(url,user,password);
             PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,regNo.trim()); ps.setDouble(2,amount); ps.executeUpdate();
        }
        return String.format("Payment recorded: %.2f",amount);
    }
}
