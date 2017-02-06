package io.cslinmiso.line.model;

/**
 * Callback to be invoked when additional information is needed for login.
 */
public interface LoginCallback {

    /**
     * User confirmation on the mobile device is required.
     * User should enter the given pin code on the device.
     *
     * @param pinCode pin code.
     */
    void onDeviceConfirmRequired(String pinCode);
}
