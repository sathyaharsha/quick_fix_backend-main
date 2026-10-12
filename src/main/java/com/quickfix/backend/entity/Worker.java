
package com.quickfix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "service_radius_km")
    private Double serviceRadiusKm = 5.0;

    @Column(name = "work_city", length = 100)
    private String workCity;

    @Column(name = "work_pincode", length = 10)
    private String workPincode;

    private Double latitude;
    private Double longitude;

    @Column(name = "is_online")
    private boolean online = false;

    @Column(name = "is_available")
    private boolean available = true;

    // Retained for compatibility; admin approval is not required.
    @Column(name = "is_verified")
    private boolean verified = false;

    public Worker() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getServiceRadiusKm() {
        return serviceRadiusKm;
    }

    public void setServiceRadiusKm(Double serviceRadiusKm) {
        this.serviceRadiusKm = serviceRadiusKm;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getWorkPincode() {
        return workPincode;
    }

    public void setWorkPincode(String workPincode) {
        this.workPincode = workPincode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
