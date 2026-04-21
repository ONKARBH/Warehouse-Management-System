package com.wms.warehouse.dto;

public class PutawaySuggestionDTO {
    private String suggestedBinCode;
    private Integer availableSpace;
    private String reason;

    public PutawaySuggestionDTO() {}

    public PutawaySuggestionDTO(String suggestedBinCode, Integer availableSpace, String reason) {
        this.suggestedBinCode = suggestedBinCode;
        this.availableSpace = availableSpace;
        this.reason = reason;
    }

    public String getSuggestedBinCode() { return suggestedBinCode; }
    public Integer getAvailableSpace() { return availableSpace; }
    public String getReason() { return reason; }

    public void setSuggestedBinCode(String suggestedBinCode) { this.suggestedBinCode = suggestedBinCode; }
    public void setAvailableSpace(Integer availableSpace) { this.availableSpace = availableSpace; }
    public void setReason(String reason) { this.reason = reason; }
}