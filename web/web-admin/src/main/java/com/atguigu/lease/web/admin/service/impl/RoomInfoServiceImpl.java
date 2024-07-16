package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.GraphInfoMapper;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Override
    public void saveOrUpdateRoomInfo(RoomSubmitVo roomSubmitVo) {
        Boolean isUpdate = roomSubmitVo.getId() != null;

        // 儲存 room
        saveOrUpdate(roomSubmitVo);

        if (isUpdate) {
            // 刪除圖片
            LambdaUpdateWrapper<GraphInfo> graphInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            graphInfoLambdaUpdateWrapper.eq(GraphInfo::getId, roomSubmitVo.getId());
            graphInfoLambdaUpdateWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoService.remove(graphInfoLambdaUpdateWrapper);
            // 刪除屬性
            LambdaUpdateWrapper<RoomAttrValue> roomAttrValueLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            roomAttrValueLambdaUpdateWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueLambdaUpdateWrapper);
            // 刪除配套
            LambdaUpdateWrapper<RoomFacility> roomFacilityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            roomFacilityLambdaUpdateWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityLambdaUpdateWrapper);
            // 刪除標籤
            LambdaUpdateWrapper<RoomLabel> roomLabelLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            roomLabelLambdaUpdateWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelLambdaUpdateWrapper);
            // 刪除支付方式
            LambdaUpdateWrapper<RoomPaymentType> roomPaymentTypeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            roomPaymentTypeLambdaUpdateWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(roomPaymentTypeLambdaUpdateWrapper);
            // 刪除租期
            LambdaUpdateWrapper<RoomLeaseTerm> roomLeaseTermLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            roomLeaseTermLambdaUpdateWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermLambdaUpdateWrapper);
        }
        // 保存圖片
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            List<GraphInfo> graphInfoList = new ArrayList<>();
            graphVoList.forEach(graphVo -> {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            });
            graphInfoService.saveBatch(graphInfoList);
        }
        // 保存屬性
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
            attrValueIds.forEach(attrValueId -> {
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(attrValueId);
                roomAttrValueList.add(roomAttrValue);
            });
            roomAttrValueService.saveBatch(roomAttrValueList);
        }
        // 保存配套
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            List<RoomFacility> roomFacilityList = new ArrayList<>();
            facilityInfoIds.forEach(facilityInfoId -> {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacility.setFacilityId(facilityInfoId);
                roomFacilityList.add(roomFacility);
            });
            roomFacilityService.saveBatch(roomFacilityList);
        }
        // 保存標籤
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelInfoIds)) {
            List<RoomLabel> roomLabelList = new ArrayList<>();
            labelInfoIds.forEach(labelInfoId -> {
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabel.setLabelId(labelInfoId);
                roomLabelList.add(roomLabel);
            });
            roomLabelService.saveBatch(roomLabelList);
        }
        // 保存支付方式
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(paymentTypeIds)) {
            List<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
            paymentTypeIds.forEach(paymentTypeId -> {
                RoomPaymentType roomPaymentType = new RoomPaymentType();
                roomPaymentType.setRoomId(roomSubmitVo.getId());
                roomPaymentType.setPaymentTypeId(paymentTypeId);
                roomPaymentTypeList.add(roomPaymentType);
            });
            roomPaymentTypeService.saveBatch(roomPaymentTypeList);
        }
        // 保存租期
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if (!CollectionUtils.isEmpty(leaseTermIds)) {
            List<RoomLeaseTerm> roomLeaseTermList = new ArrayList<>();
            leaseTermIds.forEach(leaseTermId -> {
                RoomLeaseTerm roomLeaseTerm = new RoomLeaseTerm();
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                roomLeaseTerm.setLeaseTermId(leaseTermId);
                roomLeaseTermList.add(roomLeaseTerm);
            });
            roomLeaseTermService.saveBatch(roomLeaseTermList);
        }

    }

    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo) {

        return roomInfoMapper.pageItem(page, queryVo);
    }
}




