package com.viettoria.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private String id;
    private String code;
    private String name;
    private String subName;
    private String slug;
    private String collectionId;
    private String epoch;
    private String location;
    private String shortDescription;
    private ModelSpecsDto modelSpecs;
    private String heroImage;
    private List<String> galleryImages;
    private String model3dUrl;
    private AudioGuideDto audioGuide;
    private Integer displayOrder;
    private String status;
    private Boolean isHeroRelease;
    private CollectionDto collection;
    private List<StoryBlockDto> storyBlocks;
    private List<ProductDto> relatedProducts;

    public ProductDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubName() { return subName; }
    public void setSubName(String subName) { this.subName = subName; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    public String getEpoch() { return epoch; }
    public void setEpoch(String epoch) { this.epoch = epoch; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public ModelSpecsDto getModelSpecs() { return modelSpecs; }
    public void setModelSpecs(ModelSpecsDto modelSpecs) { this.modelSpecs = modelSpecs; }

    public String getHeroImage() { return heroImage; }
    public void setHeroImage(String heroImage) { this.heroImage = heroImage; }

    public List<String> getGalleryImages() { return galleryImages; }
    public void setGalleryImages(List<String> galleryImages) { this.galleryImages = galleryImages; }

    public String getModel3dUrl() { return model3dUrl; }
    public void setModel3dUrl(String model3dUrl) { this.model3dUrl = model3dUrl; }

    public AudioGuideDto getAudioGuide() { return audioGuide; }
    public void setAudioGuide(AudioGuideDto audioGuide) { this.audioGuide = audioGuide; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsHeroRelease() { return isHeroRelease; }
    public void setIsHeroRelease(Boolean isHeroRelease) { this.isHeroRelease = isHeroRelease; }

    public CollectionDto getCollection() { return collection; }
    public void setCollection(CollectionDto collection) { this.collection = collection; }

    public List<StoryBlockDto> getStoryBlocks() { return storyBlocks; }
    public void setStoryBlocks(List<StoryBlockDto> storyBlocks) { this.storyBlocks = storyBlocks; }

    public List<ProductDto> getRelatedProducts() { return relatedProducts; }
    public void setRelatedProducts(List<ProductDto> relatedProducts) { this.relatedProducts = relatedProducts; }
}
