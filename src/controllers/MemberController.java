package controllers;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import models.Member;
import views.MemberView;
import views.Theme;

public class MemberController {
	// Cập nhật hồ sơ người dùng
	public static void handleUpdateProfile(MemberView view, Member currentMember, String newName, String newEmail) {
	    if (newName.isEmpty() && newEmail.isEmpty()) {
	        view.setProfileMsg("Vui lòng nhập ít nhất một thông tin cần thay đổi.", Theme.WARNING);
	        return;
	    }
	    if (!newEmail.isEmpty() && !newEmail.contains("@")) {
	        view.setProfileMsg("Email không hợp lệ.", Theme.ERROR); 
	        return;
	    }
	    if (!newEmail.isEmpty()) {
	        currentMember.setEmail(newEmail);
	    }
	
	    // 
	    view.refreshDisplay();
	    view.clearProfileEditFields();
	
	    if (view.getUserListener() != null) {
	        view.getUserListener().onUpdateProfile(currentMember, newName, newEmail);
	    }
	
	    view.setProfileMsg("Cập nhật thông tin thành công!", Theme.SUCCESS);
	}
	// Đổi mật khẩu người dùng
	public static void handleChangePassword(MemberView view, Member currentMember, String oldPass, String newPass, String confirm) {
	    if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
	        view.setPassMsg("Vui lòng điền đầy đủ tất cả các ô.", Theme.WARNING); 
	        return;
	    }
	    if (!oldPass.equals(currentMember.getPassword())) {
	        view.setPassMsg("Mật khẩu hiện tại không đúng.", Theme.ERROR); 
	        return;
	    }
	    if (!newPass.equals(confirm)) {
	        view.setPassMsg("Mật khẩu không khớp.", Theme.ERROR); 
	        return;
	    }
	    if (newPass.equals(oldPass)) {
	        view.setPassMsg("Mật khẩu mới không được trùng mật khẩu cũ.", Theme.WARNING); 
	        return;
	    }
	
	    currentMember.setPassword(newPass);
	    view.clearPasswordFields();
	
	    if (view.getUserListener() != null) {
	        view.getUserListener().onChangePassword(currentMember, oldPass, newPass);
	    }
	
	    view.setPassMsg("Đổi mật khẩu thành công!", Theme.SUCCESS);
	}
	// Xóa tài khoản người dùng
	public static void handleDeleteAccount(MemberView view, Member currentMember) {
	    int confirm = JOptionPane.showConfirmDialog(
	        view,
	        "Bạn chắc chắn muốn xóa tài khoản?" +
	        "Email:" + currentMember.getEmail() +
	        "Hành động này KHÔNG THỂ hoàn tác!",
	        "⚠️  Xác nhận xóa tài khoản",
	        JOptionPane.YES_NO_OPTION,
	        JOptionPane.WARNING_MESSAGE
	    );
	    if (confirm == JOptionPane.YES_OPTION) {
	        if (view.getUserListener() != null) {
	            view.getUserListener().onDeleteAccount(currentMember);
	        }
	    }
	}
}
