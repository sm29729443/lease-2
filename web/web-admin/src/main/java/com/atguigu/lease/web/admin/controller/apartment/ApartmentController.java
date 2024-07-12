package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Operation(summary = "保存或更新公寓訊息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        apartmentInfoService.saveOrUpdateApartment(apartmentSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根據條件分頁查詢公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        IPage<ApartmentItemVo> pageList = apartmentInfoService.pageItem(current, size, queryVo);
        return Result.ok(pageList);
    }

    @Operation(summary = "根據 ID 獲取公寓詳細訊息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.getDetailById(id);
        return Result.ok(apartmentDetailVo);
    }

    @Operation(summary = "根據 ID 刪除公寓訊息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        apartmentInfoService.removeByApartmentInfoId(id);
        return Result.ok();
    }

    @Operation(summary = "根據 ID 修改公寓發布狀態")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {
        LambdaUpdateWrapper<ApartmentInfo> apartmentInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apartmentInfoLambdaUpdateWrapper.eq(ApartmentInfo::getId, id);
        apartmentInfoLambdaUpdateWrapper.set(ApartmentInfo::getIsRelease, status);
        apartmentInfoService.update(apartmentInfoLambdaUpdateWrapper);
        return Result.ok();
    }

    @Operation(summary = "根據區縣 ID 查詢公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {
        LambdaQueryWrapper<ApartmentInfo> apartmentInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentInfoLambdaQueryWrapper.eq(ApartmentInfo::getDistrictId, id);
        List<ApartmentInfo> list = apartmentInfoService.list(apartmentInfoLambdaQueryWrapper);
        return Result.ok(list);
    }
}














