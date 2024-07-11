package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.CityInfo;
import com.atguigu.lease.model.entity.DistrictInfo;
import com.atguigu.lease.model.entity.ProvinceInfo;
import com.atguigu.lease.web.admin.service.CityInfoService;
import com.atguigu.lease.web.admin.service.DistrictInfoService;
import com.atguigu.lease.web.admin.service.ProvinceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "地区信息管理")
@RestController
@RequestMapping("/admin/region")
public class RegionInfoController {

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Operation(summary = "查詢省份信息列表")
    @GetMapping("province/list")
    public Result<List<ProvinceInfo>> listProvince() {
        List<ProvinceInfo> provinceInfos = provinceInfoService.list();
        return Result.ok();
    }

    @Operation(summary = "根據省份 ID 查詢城市相關列表")
    @GetMapping("city/listByProvinceId")
    public Result<List<CityInfo>> listCityInfoByProvinceId(@RequestParam Long id) {

        LambdaQueryWrapper<CityInfo> cityInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cityInfoLambdaQueryWrapper.eq(CityInfo::getProvinceId, id);
        List<CityInfo> cityInfos = cityInfoService.list(cityInfoLambdaQueryWrapper);
        return Result.ok(cityInfos);
    }

    @GetMapping("district/listByCityId")
    @Operation(summary = "根據城市 ID 查詢區縣相關列表")
    public Result<List<DistrictInfo>> listDistrictInfoByCityId(@RequestParam Long id) {
        LambdaQueryWrapper<DistrictInfo> districtInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        districtInfoLambdaQueryWrapper.eq(DistrictInfo::getCityId, id);
        List<DistrictInfo> list = districtInfoService.list(districtInfoLambdaQueryWrapper);
        return Result.ok(list);
    }

}
