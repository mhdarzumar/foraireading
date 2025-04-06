// Project structure:
// src/main/java/com/franchiseneXus/
//   - FranchiseNeXusApplication.java
//   - config/
//     - SecurityConfig.java
//   - controller/
//     - AuthController.java
//     - FranchiseController.java
//     - UserController.java
//     - ApplicationController.java
//     - BusinessController.java
//   - model/
//     - User.java
//     - Franchise.java
//     - Business.java
//     - Application.java
//     - EnumRole.java
//   - repository/
//     - UserRepository.java
//     - FranchiseRepository.java
//     - BusinessRepository.java
//     - ApplicationRepository.java
//   - service/
//     - UserService.java
//     - FranchiseService.java
//     - BusinessService.java
//     - ApplicationService.java
//     - JwtService.java
//   - exception/
//     - ResourceNotFoundException.java
//     - GlobalExceptionHandler.java
//   - dto/
//     - AuthRequest.java
//     - AuthResponse.java
//     - UserDto.java
//     - FranchiseDto.java
//     - BusinessDto.java
//     - ApplicationDto.java

// FranchiseNeXusApplication.java
package com.franchiseneXus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FranchiseNeXusApplication {
    public static void main(String[] args) {
        SpringApplication.run(FranchiseNeXusApplication.class, args);
    }
}

// Model files

// EnumRole.java
package com.franchiseneXus.model;

public enum EnumRole {
    ROLE_ADMIN,
    ROLE_FRANCHISEE,
    ROLE_FRANCHISOR
}

// User.java
package com.franchiseneXus.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private EnumRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

// Business.java
package com.franchiseneXus.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "businesses")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String industry;
    private String location;
    private String logo;
    private String website;
    private BigDecimal investmentRequired;
    private String founded;
    private Integer numberOfLocations;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}

// Franchise.java
package com.franchiseneXus.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "franchises")
public class Franchise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String industry;
    private String country;
    private String city;
    private String logo;
    private BigDecimal initialInvestment;
    private BigDecimal ongoingFees;
    private Integer contractLength;
    private String requirements;
    private String supportProvided;
    private String trainingProgram;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL)
    private List<Application> applications;
}

// Application.java
package com.franchiseneXus.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private LocalDateTime submissionDate;
    private String coverLetter;
    private String resume;
    private String financialStatement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;
}

// Repository files

// UserRepository.java
package com.franchiseneXus.repository;

import com.franchiseneXus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

// BusinessRepository.java
package com.franchiseneXus.repository;

import com.franchiseneXus.model.Business;
import com.franchiseneXus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByOwner(User owner);
    List<Business> findByIndustryContainingIgnoreCase(String industry);
}

// FranchiseRepository.java
package com.franchiseneXus.repository;

import com.franchiseneXus.model.Business;
import com.franchiseneXus.model.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {
    List<Franchise> findByBusiness(Business business);
    List<Franchise> findByIndustryContainingIgnoreCase(String industry);
    List<Franchise> findByInitialInvestmentLessThanEqual(BigDecimal maxInvestment);
    List<Franchise> findByCountryIgnoreCaseAndCityIgnoreCase(String country, String city);
}

// ApplicationRepository.java
package com.franchiseneXus.repository;

import com.franchiseneXus.model.Application;
import com.franchiseneXus.model.Franchise;
import com.franchiseneXus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicant(User applicant);
    List<Application> findByFranchise(Franchise franchise);
    List<Application> findByStatus(String status);
}

// DTO files

// AuthRequest.java
package com.franchiseneXus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}

// AuthResponse.java
package com.franchiseneXus.dto;

import com.franchiseneXus.model.EnumRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private EnumRole role;
}

// UserDto.java
package com.franchiseneXus.dto;

import com.franchiseneXus.model.EnumRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private EnumRole role;
}

// BusinessDto.java
package com.franchiseneXus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDto {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String location;
    private String logo;
    private String website;
    private BigDecimal investmentRequired;
    private String founded;
    private Integer numberOfLocations;
    private Long ownerId;
}

// FranchiseDto.java
package com.franchiseneXus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseDto {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String country;
    private String city;
    private String logo;
    private BigDecimal initialInvestment;
    private BigDecimal ongoingFees;
    private Integer contractLength;
    private String requirements;
    private String supportProvided;
    private String trainingProgram;
    private Long businessId;
}

// ApplicationDto.java
package com.franchiseneXus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private Long id;
    private String status;
    private LocalDateTime submissionDate;
    private String coverLetter;
    private String resume;
    private String financialStatement;
    private Long applicantId;
    private Long franchiseId;
}

// Service files

// JwtService.java
package com.franchiseneXus.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

// UserService.java
package com.franchiseneXus.service;

import com.franchiseneXus.dto.UserDto;
import com.franchiseneXus.exception.ResourceNotFoundException;
import com.franchiseneXus.model.User;
import com.franchiseneXus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    public UserDto createUser(UserDto userDto, String password) {
        User user = mapToEntity(userDto);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setProfileImage(userDto.getProfileImage());

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .build();
    }

    public User mapToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .profileImage(userDto.getProfileImage())
                .role(userDto.getRole())
                .build();
    }
}

// BusinessService.java
package com.franchiseneXus.service;

import com.franchiseneXus.dto.BusinessDto;
import com.franchiseneXus.exception.ResourceNotFoundException;
import com.franchiseneXus.model.Business;
import com.franchiseneXus.model.User;
import com.franchiseneXus.repository.BusinessRepository;
import com.franchiseneXus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public List<BusinessDto> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BusinessDto getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));
        return mapToDto(business);
    }

    public List<BusinessDto> getBusinessesByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        return businessRepository.findByOwner(owner).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BusinessDto> getBusinessesByIndustry(String industry) {
        return businessRepository.findByIndustryContainingIgnoreCase(industry).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BusinessDto createBusiness(BusinessDto businessDto) {
        Business business = mapToEntity(businessDto);
        Business savedBusiness = businessRepository.save(business);
        return mapToDto(savedBusiness);
    }

    public BusinessDto updateBusiness(Long id, BusinessDto businessDto) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));

        business.setName(businessDto.getName());
        business.setDescription(businessDto.getDescription());
        business.setIndustry(businessDto.getIndustry());
        business.setLocation(businessDto.getLocation());
        business.setLogo(businessDto.getLogo());
        business.setWebsite(businessDto.getWebsite());
        business.setInvestmentRequired(businessDto.getInvestmentRequired());
        business.setFounded(businessDto.getFounded());
        business.setNumberOfLocations(businessDto.getNumberOfLocations());

        Business updatedBusiness = businessRepository.save(business);
        return mapToDto(updatedBusiness);
    }

    public void deleteBusiness(Long id) {
        if (!businessRepository.existsById(id)) {
            throw new ResourceNotFoundException("Business not found with id: " + id);
        }
        businessRepository.deleteById(id);
    }

    public BusinessDto mapToDto(Business business) {
        return BusinessDto.builder()
                .id(business.getId())
                .name(business.getName())
                .description(business.getDescription())
                .industry(business.getIndustry())
                .location(business.getLocation())
                .logo(business.getLogo())
                .website(business.getWebsite())
                .investmentRequired(business.getInvestmentRequired())
                .founded(business.getFounded())
                .numberOfLocations(business.getNumberOfLocations())
                .ownerId(business.getOwner().getId())
                .build();
    }

    public Business mapToEntity(BusinessDto businessDto) {
        User owner = userRepository.findById(businessDto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + businessDto.getOwnerId()));

        return Business.builder()
                .id(businessDto.getId())
                .name(businessDto.getName())
                .description(businessDto.getDescription())
                .industry(businessDto.getIndustry())
                .location(businessDto.getLocation())
                .logo(businessDto.getLogo())
                .website(businessDto.getWebsite())
                .investmentRequired(businessDto.getInvestmentRequired())
                .founded(businessDto.getFounded())
                .numberOfLocations(businessDto.getNumberOfLocations())
                .owner(owner)
                .build();
    }
}

// FranchiseService.java
package com.franchiseneXus.service;

import com.franchiseneXus.dto.FranchiseDto;
import com.franchiseneXus.exception.ResourceNotFoundException;
import com.franchiseneXus.model.Business;
import com.franchiseneXus.model.Franchise;
import com.franchiseneXus.repository.BusinessRepository;
import com.franchiseneXus.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final BusinessRepository businessRepository;

    public List<FranchiseDto> getAllFranchises() {
        return franchiseRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FranchiseDto getFranchiseById(Long id) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found with id: " + id));
        return mapToDto(franchise);
    }

    public List<FranchiseDto> getFranchisesByBusiness(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        return franchiseRepository.findByBusiness(business).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<FranchiseDto> getFranchisesByIndustry(String industry) {
        return franchiseRepository.findByIndustryContainingIgnoreCase(industry).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<FranchiseDto> getFranchisesByMaxInvestment(BigDecimal maxInvestment) {
        return franchiseRepository.findByInitialInvestmentLessThanEqual(maxInvestment).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<FranchiseDto> getFranchisesByLocation(String country, String city) {
        return franchiseRepository.findByCountryIgnoreCaseAndCityIgnoreCase(country, city).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FranchiseDto createFranchise(FranchiseDto franchiseDto) {
        Franchise franchise = mapToEntity(franchiseDto);
        Franchise savedFranchise = franchiseRepository.save(franchise);
        return mapToDto(savedFranchise);
    }

    public FranchiseDto updateFranchise(Long id, FranchiseDto franchiseDto) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found with id: " + id));

        franchise.setName(franchiseDto.getName());
        franchise.setDescription(franchiseDto.getDescription());
        franchise.setIndustry(franchiseDto.getIndustry());
        franchise.setCountry(franchiseDto.getCountry());
        franchise.setCity(franchiseDto.getCity());
        franchise.setLogo(franchiseDto.getLogo());
        franchise.setInitialInvestment(franchiseDto.getInitialInvestment());
        franchise.setOngoingFees(franchiseDto.getOngoingFees());
        franchise.setContractLength(franchiseDto.getContractLength());
        franchise.setRequirements(franchiseDto.getRequirements());
        franchise.setSupportProvided(franchiseDto.getSupportProvided());
        franchise.setTrainingProgram(franchiseDto.getTrainingProgram());

        Franchise updatedFranchise = franchiseRepository.save(franchise);
        return mapToDto(updatedFranchise);
    }

    public void deleteFranchise(Long id) {
        if (!franchiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Franchise not found with id: " + id);
        }
        franchiseRepository.deleteById(id);
    }

    public FranchiseDto mapToDto(Franchise franchise) {
        return FranchiseDto.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .description(franchise.getDescription())
                .industry(franchise.getIndustry())
                .country(franchise.getCountry())
                .city(franchise.getCity())
                .logo(franchise.getLogo())
                .initialInvestment(franchise.getInitialInvestment())
                .ongoingFees(franchise.getOngoingFees())
                .contractLength(franchise.getContractLength())
                .requirements(franchise.getRequirements())
                .supportProvided(franchise.getSupportProvided())
                .trainingProgram(franchise.getTrainingProgram())
                .businessId(franchise.getBusiness().getId())
                .build();
    }

    public Franchise mapToEntity(FranchiseDto franchiseDto) {
        Business business = businessRepository.findById(franchiseDto.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + franchiseDto.getBusinessId()));

        return Franchise.builder()
                .id(franchiseDto.getId())
                .name(franchiseDto.getName())
                .description(franchiseDto.getDescription())
                .industry(franchiseDto.getIndustry())
                .country(franchiseDto.getCountry())
                .city(franchiseDto.getCity())
                .logo(franchiseDto.getLogo())
                .initialInvestment(franchiseDto.getInitialInvestment())
                .ongoingFees(franchiseDto.getOngoingFees())
                .contractLength(franchiseDto.getContractLength())
                .requirements(franchiseDto.getRequirements())
                .supportProvided(franchiseDto.getSupportProvided())
                .trainingProgram(franchiseDto.getTrainingProgram())
                .business(business)
                .build();
    }
}

// ApplicationService.java
// Continuing ApplicationService.java
package com.franchiseneXus.service;

import com.franchiseneXus.dto.ApplicationDto;
import com.franchiseneXus.exception.ResourceNotFoundException;
import com.franchiseneXus.model.Application;
import com.franchiseneXus.model.Franchise;
import com.franchiseneXus.model.User;
import com.franchiseneXus.repository.ApplicationRepository;
import com.franchiseneXus.repository.FranchiseRepository;
import com.franchiseneXus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;

    public List<ApplicationDto> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ApplicationDto getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return mapToDto(application);
    }

    public List<ApplicationDto> getApplicationsByApplicant(Long applicantId) {
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + applicantId));

        return applicationRepository.findByApplicant(applicant).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationDto> getApplicationsByFranchise(Long franchiseId) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found with id: " + franchiseId));

        return applicationRepository.findByFranchise(franchise).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationDto> getApplicationsByStatus(String status) {
        return applicationRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ApplicationDto createApplication(ApplicationDto applicationDto) {
        Application application = mapToEntity(applicationDto);
        application.setSubmissionDate(LocalDateTime.now());
        application.setStatus("Pending");

        Application savedApplication = applicationRepository.save(application);
        return mapToDto(savedApplication);
    }

    public ApplicationDto updateApplicationStatus(Long id, String status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);
        return mapToDto(updatedApplication);
    }

    public ApplicationDto updateApplication(Long id, ApplicationDto applicationDto) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        application.setCoverLetter(applicationDto.getCoverLetter());
        application.setResume(applicationDto.getResume());
        application.setFinancialStatement(applicationDto.getFinancialStatement());

        Application updatedApplication = applicationRepository.save(application);
        return mapToDto(updatedApplication);
    }

    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }
        applicationRepository.deleteById(id);
    }

    public ApplicationDto mapToDto(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .status(application.getStatus())
                .submissionDate(application.getSubmissionDate())
                .coverLetter(application.getCoverLetter())
                .resume(application.getResume())
                .financialStatement(application.getFinancialStatement())
                .applicantId(application.getApplicant().getId())
                .franchiseId(application.getFranchise().getId())
                .build();
    }

    public Application mapToEntity(ApplicationDto applicationDto) {
        User applicant = userRepository.findById(applicationDto.getApplicantId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + applicationDto.getApplicantId()));

        Franchise franchise = franchiseRepository.findById(applicationDto.getFranchiseId())
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found with id: " + applicationDto.getFranchiseId()));

        return Application.builder()
                .id(applicationDto.getId())
                .status(applicationDto.getStatus())
                .submissionDate(applicationDto.getSubmissionDate())
                .coverLetter(applicationDto.getCoverLetter())
                .resume(applicationDto.getResume())
                .financialStatement(applicationDto.getFinancialStatement())
                .applicant(applicant)
                .franchise(franchise)
                .build();
    }
}

// Exception files

// ResourceNotFoundException.java
package com.franchiseneXus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// GlobalExceptionHandler.java
package com.franchiseneXus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

// Config files

// SecurityConfig.java
package com.franchiseneXus.config;

import com.franchiseneXus.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// JwtAuthenticationFilter.java
package com.franchiseneXus.config;

import com.franchiseneXus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

// CustomUserDetailsService.java
package com.franchiseneXus.config;

import com.franchiseneXus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}

// Controller files

// AuthController.java
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.AuthRequest;
import com.franchiseneXus.dto.AuthResponse;
import com.franchiseneXus.dto.UserDto;
import com.franchiseneXus.model.EnumRole;
import com.franchiseneXus.model.User;
import com.franchiseneXus.repository.UserRepository;
import com.franchiseneXus.service.JwtService;
import com.franchiseneXus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Error: Email is already in use!");
        }

        UserDto userDto = UserDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();

        UserDto savedUser = userService.createUser(userDto, request.getPassword());

        User user = userRepository.findByEmail(savedUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        String jwt = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String jwt = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(authResponse);
    }

    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phoneNumber;
        private EnumRole role;

        public RegisterRequest() {}

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public EnumRole getRole() {
            return role;
        }

        public void setRole(EnumRole role) {
            this.role = role;
        }
    }
}

// UserController.java
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.UserDto;
import com.franchiseneXus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

// BusinessController.java
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.BusinessDto;
import com.franchiseneXus.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping
    public ResponseEntity<List<BusinessDto>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessDto> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<BusinessDto>> getBusinessesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(businessService.getBusinessesByOwner(ownerId));
    }

    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<BusinessDto>> getBusinessesByIndustry(@PathVariable String industry) {
        return ResponseEntity.ok(businessService.getBusinessesByIndustry(industry));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_FRANCHISOR')")
    public ResponseEntity<BusinessDto> createBusiness(@RequestBody BusinessDto businessDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(businessDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR')")
    public ResponseEntity<BusinessDto> updateBusiness(@PathVariable Long id, @RequestBody BusinessDto businessDto) {
        return ResponseEntity.ok(businessService.updateBusiness(id, businessDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        businessService.deleteBusiness(id);
        return ResponseEntity.noContent().build();
    }
}

// FranchiseController.java
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.FranchiseDto;
import com.franchiseneXus.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

        import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping
    public ResponseEntity<List<FranchiseDto>> getAllFranchises() {
        return ResponseEntity.ok(franchiseService.getAllFranchises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FranchiseDto> getFranchiseById(@PathVariable Long id) {
        return ResponseEntity.ok(franchiseService.getFranchiseById(id));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<FranchiseDto>> getFranchisesByBusiness(@PathVariable Long businessId) {
        return ResponseEntity.ok(franchiseService.getFranchisesByBusiness(businessId));
    }

    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<FranchiseDto>> getFranchisesByIndustry(@PathVariable String industry) {
        return ResponseEntity.ok(franchiseService.getFranchisesByIndustry(industry));
    }

    @GetMapping("/investment")
    public ResponseEntity<List<FranchiseDto>> getFranchisesByMaxInvestment(@RequestParam BigDecimal maxInvestment) {
        return ResponseEntity.ok(franchiseService.getFranchisesByMaxInvestment(maxInvestment));
    }

    @GetMapping("/location")
    public ResponseEntity<List<FranchiseDto>> getFranchisesByLocation(
            @RequestParam String country,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(franchiseService.getFranchisesByLocation(country, city != null ? city : ""));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_FRANCHISOR')")
    public ResponseEntity<FranchiseDto> createFranchise(@RequestBody FranchiseDto franchiseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(franchiseService.createFranchise(franchiseDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR')")
    public ResponseEntity<FranchiseDto> updateFranchise(@PathVariable Long id, @RequestBody FranchiseDto franchiseDto) {
        return ResponseEntity.ok(franchiseService.updateFranchise(id, franchiseDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFranchise(@PathVariable Long id) {
        franchiseService.deleteFranchise(id);
        return ResponseEntity.noContent().build();
    }
}

// ApplicationController.java
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.ApplicationDto;
import com.franchiseneXus.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByApplicant(@PathVariable Long applicantId) {
        return ResponseEntity.ok(applicationService.getApplicationsByApplicant(applicantId));
    }

    @GetMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByFranchise(@PathVariable Long franchiseId) {
        return ResponseEntity.ok(applicationService.getApplicationsByFranchise(franchiseId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(status));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_FRANCHISEE')")
    public ResponseEntity<ApplicationDto> createApplication(@RequestBody ApplicationDto applicationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(applicationDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_FRANCHISOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApplicationDto> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, newStatus));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_FRANCHISEE')")
    public ResponseEntity<ApplicationDto> updateApplication(
            @PathVariable Long id,
            @RequestBody ApplicationDto applicationDto) {
        return ResponseEntity.ok(applicationService.updateApplication(id, applicationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}

// PublicController.java - For non-authenticated endpoints
package com.franchiseneXus.controller;

import com.franchiseneXus.dto.BusinessDto;
import com.franchiseneXus.dto.FranchiseDto;
import com.franchiseneXus.service.BusinessService;
import com.franchiseneXus.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final BusinessService businessService;
    private final FranchiseService franchiseService;

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessDto>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    @GetMapping("/businesses/{id}")
    public ResponseEntity<BusinessDto> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @GetMapping("/businesses/industry/{industry}")
    public ResponseEntity<List<BusinessDto>> getBusinessesByIndustry(@PathVariable String industry) {
        return ResponseEntity.ok(businessService.getBusinessesByIndustry(industry));
    }

    @GetMapping("/franchises")
    public ResponseEntity<List<FranchiseDto>> getAllFranchises() {
        return ResponseEntity.ok(franchiseService.getAllFranchises());
    }

    @GetMapping("/franchises/{id}")
    public ResponseEntity<FranchiseDto> getFranchiseById(@PathVariable Long id) {
        return ResponseEntity.ok(franchiseService.getFranchiseById(id));
    }

    @GetMapping("/franchises/business/{businessId}")
    public ResponseEntity<List<FranchiseDto>> getFranchisesByBusiness(@PathVariable Long businessId) {
        return ResponseEntity.ok(franchiseService.getFr