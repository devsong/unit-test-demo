package unit.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private Long countryId;
    private String country;
    private Long provinceId;
    private String province;
    private Long cityId;
    private String city;
    private String area;
    private String detailAddress;
}
