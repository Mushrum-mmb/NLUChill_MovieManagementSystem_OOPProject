package controllers;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import views.MemberView;
import views.Theme;

public class MemberController {
	private JLabel         profileMsgLabel;
    private JLabel         passMsgLabel;
	public static void handleUpdateProfile(JTextField nameEditField, JTextField emailEditField) {
        String newName  = nameEditField.getText().trim();
        String newEmail = emailEditField.getText().trim();

        if (newName.isEmpty() && newEmail.isEmpty()) {
            setPassMsg("Vui lòng nhập ít nhất một thông tin cần thay đổi.", Theme.WARNING);
            return;
        }
        if (!newEmail.isEmpty() && !newEmail.contains("@")) {
            setProfileMsg("Email không hợp lệ.", Theme.ERROR); 
            return;
        }

        // Áp dụng thay đổi
        if (!newEmail.isEmpty()) {
            currentMember.setEmail(newEmail);
            emailDisplayLabel.setText(newEmail);
            nameDisplayLabel.setText(newEmail);
        }

        // Xóa trắng ô nhập
        nameEditField.setText(""); emailEditField.setText("");

        if (userListener != null)
            userListener.onUpdateProfile(currentMember, newName, newEmail);

        setProfileMsg("Cập nhật thông tin thành công!", Theme.SUCCESS);
    }

    public static void handleChangePassword() {
        String oldPass  = new String(oldPassField.getPassword());
        String newPass  = new String(newPassField.getPassword());
        String confirm  = new String(confirmPassField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            setPassMsg("Vui lòng điền đầy đủ tất cả các ô.", Theme.WARNING); 
            return;
        }
        if (!oldPass.equals(currentMember.getPassword())) {
            setPassMsg("Mật khẩu hiện tại không đúng.", Theme.ERROR); 
            return;
        }
        if (!newPass.equals(confirm)) {
            setPassMsg("Mật khẩu không khớp.", Theme.ERROR); 
            return;
        }
        if (newPass.equals(oldPass)) {
            setPassMsg("Mật khẩu mới không được trùng mật khẩu cũ.", Theme.WARNING); 
            return;
        }

        // Đổi mật khẩu thành công
        currentMember.setPassword(newPass);
        oldPassField.setText(""); 
        newPassField.setText(""); 
        confirmPassField.setText("");

        if (userListener != null)
            userListener.onChangePassword(currentMember, oldPass, newPass);

        setPassMsg("Đổi mật khẩu thành công!", Theme.SUCCESS);
    }

    public static void handleDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "<html><b>Bạn chắc chắn muốn xóa tài khoản?</b><br>" +
            "Email: <b>" + currentMember.getEmail() + "</b><br><br>" +
            "<font color='red'>Hành động này KHÔNG THỂ hoàn tác!</font></html>",
            "⚠️  Xác nhận xóa tài khoản",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            if (userListener != null) userListener.onDeleteAccount(currentMember);
        }
    }
    public static void setProfileMsg(String msg, Color color) {
        profileMsgLabel.setText(msg); 
        profileMsgLabel.setForeground(color);
    }
    public static void setPassMsg(String msg, Color color) {
        passMsgLabel.setText(msg); 
        passMsgLabel.setForeground(color);
    }
}
