package com.app.ecom.ecommerce_app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	
	@Column(length = 500)
	private String description;
	private BigDecimal price;
	private int quantity;
	private String imageUrl;
	
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	private Category category;
	
	@ManyToMany
	@JoinTable(name="product_tags", joinColumns=@JoinColumn(name="product_id"), inverseJoinColumns=@JoinColumn(name="tag_id"))
	private List<Tag> tag;
	
	@OneToMany(mappedBy="product", cascade=CascadeType.ALL)
	private List<ProductReview> reviews;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	// @CreatedTime annotation can be used
	private LocalDateTime createdDate=LocalDateTime.now();
	// @UpdatedTime annotation can be used
	private LocalDateTime updatedDate=LocalDateTime.now();
	
	private boolean active=true;
	
}
