package ninjabrainbot.data.information;

import java.util.List;

import ninjabrainbot.data.IDataState;
import ninjabrainbot.data.calculator.ICalculatorResult;
import ninjabrainbot.data.stronghold.Chunk;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;
import ninjabrainbot.util.I18n;

public class CombinedCertaintyInformationProvider extends InformationMessageProvider {

	private List<Chunk> predictions;

	public CombinedCertaintyInformationProvider(IDataState dataState, NinjabrainBotPreferences preferences) {
		super(preferences.informationCombinedCertaintyEnabled);
		sh.add(dataState.calculatorResult().subscribe(this::updateInformationMessage));
	}

	private void updateInformationMessage(ICalculatorResult calculatorResult) {
		predictions = null;
		if (calculatorResult != null && calculatorResult.success())
			predictions = calculatorResult.getTopChunks();
		raiseInformationMessageChanged();
	}

	@Override
	protected boolean shouldShowInformationMessage() {
		if (predictions == null || predictions.size() < 2)
			return false;
		Chunk chunk0 = predictions.get(0);
		Chunk chunk1 = predictions.get(1);
		if (!chunk0.isNeighboring(chunk1))
			return false;
		return chunk0.weight + chunk1.weight > 0.95;
	}

	@Override
	protected InformationMessage getInformationMessage() {
		Chunk chunk0 = predictions.get(0);
		Chunk chunk1 = predictions.get(1);
		double combinedProbability = chunk0.weight + chunk1.weight;
		int netherX = (chunk0.netherX() + chunk1.netherX()) / 2;
		int netherZ = (chunk0.netherZ() + chunk1.netherZ()) / 2;
		return new InformationMessage(InformationType.Info, I18n.get("information.top_two_chunks_are_neighboring", netherX, netherZ, combinedProbability * 100));
	}

}
