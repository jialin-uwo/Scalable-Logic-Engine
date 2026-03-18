public class TestDataLoader {
    public static void main(String[] args) {
        try {
            System.out.println("Loading game data...");
            DataLoader loader = new DataLoader();
            GameData data = loader.loadGameData("assets/data/DataFile.json");
            System.out.println("Success! Loaded:");
            System.out.println("- Locations: " + data.getLocations().size());
            System.out.println("- Objects: " + data.getObjects().getAllObjects().size());
            System.out.println("- Characters: " + data.getCharacters().size());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
