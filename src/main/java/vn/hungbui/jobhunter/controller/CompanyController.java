package vn.hungbui.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hungbui.jobhunter.domain.Company;
import vn.hungbui.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hungbui.jobhunter.service.CompanyService;

import com.turkraft.springfilter.boot.Filter;
import vn.hungbui.jobhunter.util.annotation.ApiMessage;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
    }

    //Phân trang thủ công bằng cách khai báo 2 tham số current và pageSize có KDL Optional
    //Thay kdl List<Company> thì ta trả về kdl ResponseEntity<ResultPaginationDTO>
//    @GetMapping("/companies")
//    public ResponseEntity<ResultPaginationDTO> getCompany(
//            @RequestParam("current") Optional<String> currentOptional,
//            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
//        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
//        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
//
//        int current = Integer.parseInt(sCurrent);
//        int pageSize = Integer.parseInt(sPageSize);
//
//        Pageable pageable = PageRequest.of(current - 1, pageSize);
//        return ResponseEntity.ok(this.companyService.handleGetCompany(pageable));
//    }

    //Cách 2: Specification cho phép lọc dữ liệu theo các điều kiện được cung cấp trước
    //        Pageable được nhận trực tiếp từ yêu cầu HTTP GET, giúp phân trang một cách tự động mà không cần xử lý thủ công như đoạn 1.
    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
    }


    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
        Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
