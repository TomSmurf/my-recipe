package be.ucll.myrecipe.server.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordChangeDTO {

    private String currentPassword;

    @NotBlank
    @Size(min = 4, max = 100)
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
