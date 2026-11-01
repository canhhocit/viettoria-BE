package com.viettoria.dto;

public class ModelSpecsDto {
    private String scale;
    private double heightCm;
    private double widthCm;
    private double weightGrams;
    private String material;
    private int partsCount;
    private String finishing;

    public ModelSpecsDto() {}

    public ModelSpecsDto(String scale, double heightCm, double widthCm, double weightGrams, String material, int partsCount, String finishing) {
        this.scale = scale;
        this.heightCm = heightCm;
        this.widthCm = widthCm;
        this.weightGrams = weightGrams;
        this.material = material;
        this.partsCount = partsCount;
        this.finishing = finishing;
    }

    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWidthCm() { return widthCm; }
    public void setWidthCm(double widthCm) { this.widthCm = widthCm; }

    public double getWeightGrams() { return weightGrams; }
    public void setWeightGrams(double weightGrams) { this.weightGrams = weightGrams; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public int getPartsCount() { return partsCount; }
    public void setPartsCount(int partsCount) { this.partsCount = partsCount; }

    public String getFinishing() { return finishing; }
    public void setFinishing(String finishing) { this.finishing = finishing; }
}
