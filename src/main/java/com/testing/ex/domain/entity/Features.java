package com.testing.ex.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Features entity represents the technical specifications of a product.
 */
//@Entity
//@Table(name = "features")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Features {

//  @Id
//  @GeneratedValue(strategy = GenerationType.AUTO)
//  private Long id;

  @Column(name = "ram", nullable = false)
  private String ram;

  @Column(name = "storage", nullable = false)
  private String storage;

  @Column(name = "battery", nullable = false)
  private String battery;

  @OneToOne(mappedBy = "features", fetch = FetchType.LAZY)
  private Product product;

}


