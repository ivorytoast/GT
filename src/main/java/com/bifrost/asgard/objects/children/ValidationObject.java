package com.bifrost.asgard.objects.children;

import java.util.ArrayList;
import java.util.List;

public class ValidationObject {

    private boolean isFullyValidated;
    private List<String> errors;

    public ValidationObject() {
        this.isFullyValidated = false;
        this.errors = new ArrayList<>();
    }

    public boolean isFullyValidated() {
        return isFullyValidated;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setFullyValidated(boolean fullyValidated) {
        isFullyValidated = fullyValidated;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean containsErrors() {
        return this.errors.size() > 0;
    }

    public String errorsToString() {
        StringBuilder sb = new StringBuilder("");
        boolean firstPass = true;
        for (String error : errors) {
            if (!firstPass) {
                sb.append(" & ");
            } else {
                firstPass = false;
            }
            sb.append(error);
        }
        return sb.toString();
    }

}
