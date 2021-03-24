package com.gt.common;

import java.util.List;

import com.gt.common.api.OrderRequest;
import com.gt.common.data.OrderData;
import com.gt.common.view.OrderView;

public class Converter {

	public static void requestToDataConverter(OrderRequest request, OrderData order) {
		order.setId(null);
		order.setSymbol(request.getSymbol());
		order.setQuantity(request.getQuantity());
		order.setPrice(request.getPrice());
		order.setSide(request.getSide());
		order.setQuantityRemaining(request.getQuantity());
	}

	public static void viewToDataModelConverter(OrderView orderView, OrderData order) {
		order.setId(orderView.getId());
		order.setSymbol(orderView.getSymbol());
		order.setQuantity(orderView.getQuantity());
		order.setPrice(orderView.getPrice());
		order.setSide(orderView.getSide());
	}
	
	public static void dataToViewModelConverter(OrderView viewModel, OrderData dataModel) {
		viewModel.setId(dataModel.getId());
		viewModel.setSymbol(dataModel.getSymbol());
		viewModel.setQuantity(dataModel.getQuantity());
		viewModel.setPrice(dataModel.getPrice());
		viewModel.setSide(dataModel.getSide());
	}

	public static void dataToViewModelConverterForList(List<OrderView> viewModel, List<OrderData> dataModel) {
		for (int i=0; i<dataModel.size(); i++) {
			OrderView orderView = new OrderView();
			dataToViewModelConverter(orderView,dataModel.get(i));
			viewModel.add(orderView);
		}
	}

	public static void dataToViewModelConverterForSingleOrder(OrderView viewModel, OrderData dataModel) {
		dataToViewModelConverter(viewModel, dataModel);
	}

}
