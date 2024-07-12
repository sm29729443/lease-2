package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    // 圖片 Table
    @Autowired
    private GraphInfoService graphInfoService;

    // 配套中間表
    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    // 雜費中間表
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    // 標籤中間表
    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        // 判斷是新增還是刪除
        Boolean isUpdate = apartmentSubmitVo.getId() != null;

        // 先儲存公寓Table的資料
        saveOrUpdate(apartmentSubmitVo);

        if (isUpdate) {
            // 刪除圖片列表
            LambdaUpdateWrapper<GraphInfo> graphInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<GraphInfo>();
            graphInfoLambdaUpdateWrapper.eq(GraphInfo::getId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphInfoLambdaUpdateWrapper);

            // 刪除配套列表
            LambdaUpdateWrapper<ApartmentFacility> apartmentFacilityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apartmentFacilityLambdaUpdateWrapper.eq(ApartmentFacility::getId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(apartmentFacilityLambdaUpdateWrapper);

            // 刪除雜費列表
            LambdaUpdateWrapper<ApartmentFeeValue> apartmentFeeValueLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apartmentFeeValueLambdaUpdateWrapper.eq(ApartmentFeeValue::getId, apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueLambdaUpdateWrapper);

            // 刪除標籤列表
            LambdaUpdateWrapper<ApartmentLabel> apartmentLabelLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apartmentLabelLambdaUpdateWrapper.eq(ApartmentLabel::getId, apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelLambdaUpdateWrapper);
        }

        // 插入圖片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            List<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }

        //插入配套列表
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            List<ApartmentFacility> apartmentFacilityList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityInfoId);
                apartmentFacilityList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(apartmentFacilityList);
        }
        //插入雜費列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            List<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);
        }
        //插入標籤列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);
        }
    }

    @Override
    public IPage<ApartmentItemVo> pageItem(long current, long size, ApartmentQueryVo queryVo) {
        IPage<ApartmentItemVo> page = new Page<>(current, size);
        return apartmentInfoMapper.pageItem(page, queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        // 1. 查詢公寓訊息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        // 2. 查詢圖片列表
        List<GraphVo> graphVoList= graphInfoMapper.selectListByItemTypeAndId(id);
        // 3. 查詢標籤列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);
        // 4. 查詢雜費列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);
        // 5. 查詢配套訊息
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);

        // 6. 組裝結果
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);
        return apartmentDetailVo;

    }

    @Override
    public void removeByApartmentInfoId(Long id) {
        // 刪除 apartment_info Table
        removeById(id);
        // 刪除 apartment_facility Table
        LambdaUpdateWrapper<ApartmentFacility> apartmentFacilityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apartmentFacilityLambdaUpdateWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(apartmentFacilityLambdaUpdateWrapper);
        // 刪除 apartmnet_fee_value Table
        LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentFeeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(apartmentFeeValueLambdaQueryWrapper);
        // 刪除 apartment_label  Table
        LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentLabelLambdaQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(apartmentLabelLambdaQueryWrapper);
        // 刪除 graph_info Table
        LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphInfoLambdaQueryWrapper);
    }
}




