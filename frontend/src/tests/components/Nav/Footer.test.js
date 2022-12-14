import { render, screen } from "@testing-library/react";
import Footer, {space} from "main/components/Nav/Footer";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

describe("Footer tests", () => {
    test("space stands for a space", () => {
        expect(space).toBe(" ");
      });
    
    
      test("renders without crashing", () => {
        const systemInfo = systemInfoFixtures.showingBoth;
        render(<Footer systemInfo={systemInfo}  />);
      });
    
      test("Links are correct", async () => {
        const SysInfo = systemInfoFixtures.showingBoth;
        render(<Footer systemInfo={SysInfo} />)
        expect(screen.getByTestId("footer-class-website-link")).toHaveAttribute(
          "href",
          "https://ucsb-cs156.github.io"
        );
        expect(screen.getByTestId("footer-ucsb-link")).toHaveAttribute(
          "href",
          "https://ucsb.edu"
        );
        expect(screen.getByTestId("footer-source-code-link")).toHaveAttribute(
          "href",
          SysInfo.sourceRepoUrl
        );

        expect(screen.getByTestId("footer-sticker-link")).toHaveAttribute(
            "href",
            "https://www.as.ucsb.edu/sticker-packs"
          );
          expect(screen.getByTestId("footer-course-search-link")).toHaveAttribute(
            "href",
            "https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx"
          );

      });
});
