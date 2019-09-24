package pe.ebenites.alldemo.models;

public class User {

    private Integer id;
    private String firstname;
    private String lastname;
    private String fullname;
    private String email;
    private String phonenumber;
    private String birthdate;
    private String gender;
    private String countries_id;
    private String districts_id;
    private District district;
    private Integer schools_id;
    private Integer roles_id;
    private Role role;
    private String token;
    private Boolean isnew;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountries_id() {
        return countries_id;
    }

    public void setCountries_id(String countries_id) {
        this.countries_id = countries_id;
    }

    public String getDistricts_id() {
        return districts_id;
    }

    public void setDistricts_id(String districts_id) {
        this.districts_id = districts_id;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Integer getSchools_id() {
        return schools_id;
    }

    public void setSchools_id(Integer schools_id) {
        this.schools_id = schools_id;
    }

    public Integer getRoles_id() {
        return roles_id;
    }

    public void setRoles_id(Integer roles_id) {
        this.roles_id = roles_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIsnew() {
        return isnew;
    }

    public void setIsnew(Boolean isnew) {
        this.isnew = isnew;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", gender='" + gender + '\'' +
                ", countries_id='" + countries_id + '\'' +
                ", districts_id='" + districts_id + '\'' +
                ", district=" + district +
                ", schools_id=" + schools_id +
                ", roles_id=" + roles_id +
                ", role=" + role +
                ", token='" + token + '\'' +
                ", isnew=" + isnew +
                '}';
    }
}
